package com.orderservice.controller;

import com.orderservice.dto.ErrorResponseDto;
import com.orderservice.security.CustomPrincipal;
import com.orderservice.dto.UserOrderDto;
import com.orderservice.dto.UserOrderPreviewDto;
import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.OrderParams;
import com.orderservice.service.UserOrderFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/orders")
public class UserOrderController {
    private final UserOrderFacade userOrderFacade;

    @Operation(
            summary = "Place new order",
            description = "Creates new order and returns payment session link"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order successfully created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid input data",
                                            value = """
                                                    {
                                                        "phoneNumber": "must not be blank"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Reservation failed because of insufficient stock or product was disabled.",
                                            value = """
                                                    {
                                                        "code": "Bad request",
                                                        "status": 400,
                                                        "message": "Product car is not available or inactive"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User does not have a cart.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PostMapping
    public ResponseEntity<String> createOrder(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateOrderRequest body
    ) {
        String redirectUrl = userOrderFacade.placeOrder(body, principal.getId());

        return ResponseEntity.ok(redirectUrl);
    }


    @Operation(
            summary = "Get order by id",
            description = "Returns order dto by id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order successfully found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserOrderDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserOrderDto> getOrder(
            @PathVariable UUID id
    ) {
        UserOrderDto order = userOrderFacade.getUserOrderDtoById(id);

        return ResponseEntity.ok().body(order);
    }

    @Operation(
            summary = "Search orders by parameters",
            description = "Returns a page of found orders"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The page of found offers",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<Page<UserOrderPreviewDto>> getOrderPage(
            @AuthenticationPrincipal CustomPrincipal principal,
            OrderParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<UserOrderPreviewDto> orders = userOrderFacade.getOrdersByUserId(principal.getId(), pageable, body);

        return ResponseEntity.ok().body(orders);
    }
}

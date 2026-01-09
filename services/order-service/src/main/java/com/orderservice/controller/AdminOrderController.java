package com.orderservice.controller;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.AdminOrderPreviewDto;
import com.orderservice.dto.AdminOrderParams;
import com.orderservice.dto.ErrorResponseDto;
import com.orderservice.dto.UpdateContactInfoRequest;
import com.orderservice.dto.UpdateOrderStatusRequest;
import com.orderservice.dto.UserOrderDto;
import com.orderservice.enums.OrderAdminSortByEnum;
import com.orderservice.service.AdminOrderFacade;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    private final AdminOrderFacade adminOrderService;

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
    public ResponseEntity<AdminOrderDto> getOrderById(
            @PathVariable("id") UUID id
    ) {
        AdminOrderDto order = adminOrderService.getOrder(id);

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
    public ResponseEntity<Page<AdminOrderPreviewDto>> getAllOrders(
            AdminOrderParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "ID") OrderAdminSortByEnum sortBy
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdminOrderPreviewDto> orders = adminOrderService.getOrders(body, pageable);

        return ResponseEntity.ok().body(orders);
    }

    @Operation(
            summary = "Update order contact info",
            description = "Updates order contact info and returns updated offer dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminOrderDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid input data",
                                            value = """
                                                    {
                                                        "address": "must not be blank"
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
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PatchMapping("/{id}/contact-info")
    public ResponseEntity<AdminOrderDto> updateContactInfo(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateContactInfoRequest body
    ) {
        AdminOrderDto order = adminOrderService.updateOrderContactInfo(id, body);

        return ResponseEntity.ok().body(order);
    }

    @Operation(
            summary = "Update order status",
            description = "Updates order status and returns updated offer dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminOrderDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid input data",
                                            value = """
                                                    {
                                                        "targetStatus": "must not be provided"
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
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminOrderDto> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateOrderStatusRequest body
    ) {
        AdminOrderDto order = adminOrderService.updateOrderStatus(id, body);

        return ResponseEntity.ok(order);
    }


}

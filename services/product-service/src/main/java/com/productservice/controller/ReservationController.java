package com.productservice.controller;

import com.productservice.dto.ErrorResponseDto;
import com.productservice.dto.ReservationDto;
import com.productservice.dto.ReservationPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.AdminReservationParams;
import com.productservice.enums.ReservationStatusEnum;
import com.productservice.enums.ReservationAdminSortByEnum;
import com.productservice.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(
            summary = "Find reservation by id",
            description = "Returns found product dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation successfully found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProductDto.class)
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
                    description = "Reservation not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservation(
            @PathVariable UUID id
    ) {
        ReservationDto reservation = reservationService.getReservationAdminDto(id);

        return ResponseEntity.ok(reservation);
    }

    @Operation(
            summary = "Search reservations by params",
            description = "Returns a page of reservations"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Found reservations"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @GetMapping
    public ResponseEntity<Page<ReservationPreviewDto>> getReservationPage(
            AdminReservationParams params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "CREATED_AT") ReservationAdminSortByEnum sortBy
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReservationPreviewDto> reservationPage = reservationService.getReservationList(pageable, params);

        return ResponseEntity.ok(reservationPage);
    }
}

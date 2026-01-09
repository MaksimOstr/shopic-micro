package com.productservice.controller;

import com.productservice.dto.AdminBrandDto;
import com.productservice.dto.ErrorResponseDto;
import com.productservice.dto.request.AdminBrandParams;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.enums.BrandAdminSortByEnum;
import com.productservice.services.BrandService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {
    private final BrandService brandService;

    @Operation(
            summary = "Search brands",
            description = "Returns a page of brands"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Found brands"
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
    public ResponseEntity<Page<AdminBrandDto>> getAll(
            AdminBrandParams params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ID") BrandAdminSortByEnum sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdminBrandDto> brands = brandService.searchAdminBrands(params, pageable);

        return ResponseEntity.ok().body(brands);
    }

    @Operation(
            summary = "Create new brand",
            description = "Creates new brand and returns its dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Brand successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminBrandDto.class)
                    )
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
                                                        "isActive": "must not be provided"
                                                    }
                                                    """
                                    ),
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
                    responseCode = "409",
                    description = "Brand with the same name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PostMapping
    public ResponseEntity<AdminBrandDto> create(
            @RequestBody @Valid CreateBrandRequest body
    ) {
        AdminBrandDto brand = brandService.create(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @Operation(
            summary = "Update brand",
            description = "Updates brand and returns updated brand dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Brand successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminBrandDto.class)
                    )
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
                                                        "isActive": "must not be provided"
                                                    }
                                                    """
                                    ),
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
                    description = "Brand with provided id was not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Brand with the same name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdminBrandDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateBrandRequest body
    ) {
        AdminBrandDto brand = brandService.updateBrand(id, body);

        return ResponseEntity.ok(brand);
    }
}

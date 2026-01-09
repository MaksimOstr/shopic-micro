package com.productservice.controller;


import com.productservice.dto.AdminProductDto;
import com.productservice.dto.AdminProductPreviewDto;
import com.productservice.dto.ErrorResponseDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.enums.ProductAdminSortByEnum;
import com.productservice.services.ProductFacade;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductFacade productFacade;

    @Operation(
            summary = "Find product by id",
            description = "Returns found product dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminProductDto.class)
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
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDto> getProduct(
            @PathVariable UUID id
    ) {
        AdminProductDto product = productFacade.getAdminProduct(id);

        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Search products",
            description = "Returns a page of products"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Found products"
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
    public ResponseEntity<Page<AdminProductPreviewDto>> getPageOfProductsByFilter(
            AdminProductParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "PRICE") ProductAdminSortByEnum sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdminProductPreviewDto> products = productFacade.getProductsByFilters(body, pageable);

        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Create new product",
            description = "Creates new product and returns its dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminProductDto.class)
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
                                                        "name": "must not be provided"
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
                    description = "Category or brand was not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PostMapping
    public ResponseEntity<AdminProductDto> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile
    ) {
        AdminProductDto product = productFacade.createProduct(body, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(
            summary = "Update product",
            description = "Updates product and returns its dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminProductDto.class)
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
                                                        "stockQuantity": "must be greater or equal to 0"
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
                    description = "Product, brand or category was not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AdminProductDto> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateProductRequest body
    ) {
        AdminProductDto product = productFacade.updateProduct(id, body);

        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Update product image",
            description = "Updates product image and returns its dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product image successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminProductDto.class)
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
                    description = "Product was not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PatchMapping("/{id}/image")
    public ResponseEntity<Void> updateProductImage(
        @PathVariable UUID id,
        @RequestPart("image") MultipartFile imageFile
    ) {
        productFacade.changeProductImage(id, imageFile);

        return ResponseEntity.ok().build();
    }
}

package com.productservice.controller;


import com.productservice.dto.AdminProductDto;
import com.productservice.dto.AdminProductPreviewDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.enums.ProductAdminSortByEnum;
import com.productservice.services.ProductFacade;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private final ProductFacade productFacade;


    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDto> getProduct(
            @PathVariable UUID id
    ) {
        AdminProductDto product = productFacade.getAdminProduct(id);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
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

    @PostMapping
    public ResponseEntity<AdminProductDto> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile
    ) {
        AdminProductDto product = productFacade.createProduct(body, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminProductDto> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateProductRequest body
    ) {
        AdminProductDto product = productFacade.updateProduct(id, body);

        return ResponseEntity.ok(product);
    }


    @PatchMapping("/{id}/image")
    public ResponseEntity<Void> updateProductImage(
        @PathVariable UUID id,
        @RequestPart("image") MultipartFile imageFile
    ) {
        productFacade.changeProductImage(id, imageFile);

        return ResponseEntity.ok().build();
    }
}

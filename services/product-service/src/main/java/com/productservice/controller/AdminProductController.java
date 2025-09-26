package com.productservice.controller;


import com.productservice.dto.AdminProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.enums.ProductAdminSortByEnum;
import com.productservice.services.facades.AdminProductFacade;
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
    private final AdminProductFacade adminProductFacade;


    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDto> getProduct(
            @PathVariable long id
    ) {
        AdminProductDto product = adminProductFacade.getAdminProduct(id);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<AdminProductDto> getProductBySku(
            @PathVariable UUID sku
    ) {
        AdminProductDto product = adminProductFacade.getAdminProduct(sku);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductAdminPreviewDto>> getPageOfProductsByFilter(
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
        Page<ProductAdminPreviewDto> products = adminProductFacade.getProductsByFilters(body, pageable);

        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<AdminProductDto> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile
    ) {
        AdminProductDto product = adminProductFacade.createProduct(body, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminProductDto> updateProduct(
            @PathVariable long id,
            @RequestBody @Valid UpdateProductRequest body
    ) {
        AdminProductDto product = adminProductFacade.updateProduct(id, body);

        return ResponseEntity.ok(product);
    }


    @PatchMapping("/{id}/image")
    public ResponseEntity<Void> updateProductImage(
        @PathVariable long id,
        @RequestPart("image") MultipartFile imageFile
    ) {
        adminProductFacade.changeProductImage(id, imageFile);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Void> archiveProduct(
            @PathVariable long id
    ) {
        adminProductFacade.archiveProduct(id);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProduct(
            @PathVariable long id
    ) {
        adminProductFacade.activateProduct(id);

        return ResponseEntity.ok().build();
    }
}

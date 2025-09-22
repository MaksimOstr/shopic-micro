package com.productservice.controller;


import com.productservice.config.security.model.CustomPrincipal;
import com.productservice.dto.AdminProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.services.products.AdminProductFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private final AdminProductFacade adminProductFacade;


    @PostMapping
    public CompletableFuture<ResponseEntity<Product>> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile
    ) {
        return adminProductFacade.createProduct(body, imageFile)
                .thenApply(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDto> getProduct(
            @PathVariable long id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        AdminProductDto product = adminProductFacade.getAdminProduct(id, principal.getId());

        return ResponseEntity.ok(product);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable long id,
            @RequestBody @Valid UpdateProductRequest body
    ) {
        Product product = adminProductFacade.updateProduct(body, id);

        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<ProductAdminPreviewDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute AdminProductParams body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductAdminPreviewDto> products = adminProductFacade.getProductsByFilters(body, pageable, principal.getId());

        return ResponseEntity.ok(products);
    }


    @PatchMapping("/{id}/image")
    public CompletableFuture<ResponseEntity<Void>> updateProductImage(
        @PathVariable long id,
        @RequestPart("image") MultipartFile imageFile
    ) {
        return adminProductFacade.updateProductImage(id, imageFile)
                .thenApply(_ -> ResponseEntity.ok().build());
    }


    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(
            @PathVariable UUID sku
    ) {
        Product product = adminProductFacade.getProductBySku(sku);

        return ResponseEntity.ok(product);
    }
}

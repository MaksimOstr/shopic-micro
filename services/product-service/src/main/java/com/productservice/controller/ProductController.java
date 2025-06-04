package com.productservice.controller;

import com.productservice.dto.CustomPrincipal;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public CompletableFuture<ResponseEntity<Product>> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        return productService.create(body, imageFile, principal.getId())
                .thenApply(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Product> updateProduct(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable long id,
            @RequestBody @Valid UpdateProductRequest body
    ) {
        Product product = productService.updateProduct(body, principal.getId(), id);

        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/{id}/image")
    public ResponseEntity<String> updateProductImage(
        @AuthenticationPrincipal CustomPrincipal principal,
        @PathVariable long id,
        @RequestPart("image") MultipartFile imageFile
    ) {
        productService.updateProductImage(principal.getId(), id, imageFile);

        String message = "Product image updated successfully";

        return ResponseEntity.ok(message);
    }



    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<Product>> getSellerProducts(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getPageOfSellerProducts(principal.getId(), pageable);

        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(
            @PathVariable long id
    ) {
        Product product = productService.getProductById(id);

        return ResponseEntity.ok(product);
    }
}

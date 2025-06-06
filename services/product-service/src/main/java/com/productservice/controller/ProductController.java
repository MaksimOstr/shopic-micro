package com.productservice.controller;


import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.GetProductsByFilters;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.services.ProductService;
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
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<Product>> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile
    ) {
        return productService.create(body, imageFile)
                .thenApply(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable long id,
            @RequestBody @Valid UpdateProductRequest body
    ) {
        Product product = productService.updateProduct(body, id);

        return ResponseEntity.ok(product);
    }


    @PatchMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateProductImage(
        @PathVariable long id,
        @RequestPart("image") MultipartFile imageFile
    ) {
        productService.updateProductImage(id, imageFile);

        String message = "Product image updated successfully";

        return ResponseEntity.ok(message);
    }


    @GetMapping("/filter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<Product>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody GetProductsByFilters body
            ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productService.findProductsByFilters(body, pageable);

        return ResponseEntity.ok(products);
    }


    @GetMapping("/sku")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> getProductBySku(
            @RequestParam UUID sku
    ) {
        Product product = productService.getProductBySku(sku);

        return ResponseEntity.ok(product);
    }


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<Product>> getPageOfProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> pageOfProducts = productService.getPageOfProducts(pageable);

        return ResponseEntity.ok(pageOfProducts);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Product> getProduct(
            @PathVariable long id
    ) {
        Product product = productService.getProductById(id);

        return ResponseEntity.ok(product);
    }
}

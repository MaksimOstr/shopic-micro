package com.productservice.controller;


import com.productservice.config.security.model.CustomPrincipal;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.GetProductsByFilters;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
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


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(
            @PathVariable long id
    ) {
        productService.deleteProductById(id);

        String message = "Product with id " + id + " has been deleted";

        return ResponseEntity.ok(message);
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
    public ResponseEntity<Page<ProductDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody GetProductsByFilters body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductDto> products = productService.findProductsByFilters(body, pageable, principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/liked")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductDto>> getLikedProducts(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<ProductDto> products = productService.getLikedProducts(principal.getId());

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
    public ResponseEntity<Page<ProductDto>> getPageOfProducts(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductDto> products = productService.getPageOfProducts(pageable, principal.getId());

        return ResponseEntity.ok(products);
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

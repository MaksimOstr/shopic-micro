package com.productservice.controller;


import com.productservice.config.security.model.CustomPrincipal;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
import com.productservice.services.ProductImageService;
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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private final ProductService productService;


    @PostMapping
    public CompletableFuture<ResponseEntity<Product>> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile
    ) {
        return productService.create(body, imageFile)
                .thenApply(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable long id
    ) {
        productService.deleteProductById(id);

        String message = "Product with id " + id + " has been deleted";

        return ResponseEntity.ok(message);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(
            @PathVariable long id
    ) {
        Product product = productService.getProductById(id);

        return ResponseEntity.ok(product);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable long id,
            @RequestBody @Valid UpdateProductRequest body
    ) {
        Product product = productService.updateProduct(body, id);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody AdminProductParams body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductDto> products = productService.findAdminProductsByFilters(body, pageable, principal.getId());

        return ResponseEntity.ok(products);
    }


    @PatchMapping("/{id}/image")
    public CompletableFuture<ResponseEntity<String>> updateProductImage(
        @PathVariable long id,
        @RequestPart("image") MultipartFile imageFile
    ) {
        return productService.updateProductImage(id, imageFile)
                .thenApply(_ -> ResponseEntity.ok().build());
    }


    @GetMapping("/sku")
    public ResponseEntity<Product> getProductBySku(
            @RequestParam UUID sku
    ) {
        Product product = productService.getProductBySku(sku);

        return ResponseEntity.ok(product);
    }
}

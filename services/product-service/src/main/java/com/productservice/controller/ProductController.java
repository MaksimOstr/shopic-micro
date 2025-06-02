package com.productservice.controller;

import com.productservice.dto.CustomPrincipal;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.entity.Product;
import com.productservice.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") @Valid CreateProductRequest body,
            @RequestPart("image") MultipartFile imageFile,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Product product = productService.create(body, imageFile, principal.getId());

        return ResponseEntity.ok(product);
    }
}

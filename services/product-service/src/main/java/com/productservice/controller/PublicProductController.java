package com.productservice.controller;

import com.productservice.config.security.model.CustomPrincipal;
import com.productservice.dto.request.ProductParams;
import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
import com.productservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@PreAuthorize("hasRole('USER')")
public class PublicProductController {
    private final ProductService productService;


    @GetMapping("/filter")
    public ResponseEntity<Page<ProductDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody ProductParams body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductDto> products = productService.findPublicProductsByFilters(body, pageable, principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getPageOfProducts(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductDto> products = productService.getPageOfProducts(pageable, principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/liked")
    public ResponseEntity<List<ProductDto>> getLikedProducts(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<ProductDto> products = productService.getLikedProducts(principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(
            @PathVariable long id
    ) {
        Product product =  productService.getProductById(id);

        return ResponseEntity.ok(product);
    }
}

package com.productservice.controller;

import com.productservice.config.security.model.CustomPrincipal;
import com.productservice.dto.request.ProductParams;
import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
import com.productservice.services.products.UserProductFacade;
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
    private final UserProductFacade productFacade;


    @GetMapping
    public ResponseEntity<Page<ProductDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody ProductParams body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductDto> products = productFacade.getProductsByFilters(body, pageable, principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/liked")
    public ResponseEntity<List<ProductDto>> getLikedProducts(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<ProductDto> products = productFacade.getLikedProducts(principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/active/{id}")
    public ResponseEntity<Product> getActiveProduct(
            @PathVariable long id
    ) {
        Product product = productFacade.getProduct(id);

        return ResponseEntity.ok(product);
    }
}

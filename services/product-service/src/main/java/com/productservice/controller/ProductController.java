package com.productservice.controller;


import com.productservice.dto.LikedProductDto;
import com.productservice.dto.UserProductPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
import com.productservice.security.CustomPrincipal;
import com.productservice.services.ProductFacade;
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
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductFacade productFacade;

    @GetMapping("/{id}")
    public ResponseEntity<UserProductDto> getProduct(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UUID userId = principal == null ? null : principal.id();
        UserProductDto product = productFacade.getUserProduct(id, userId);

        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<UserProductPreviewDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            UserProductParams body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UUID userId = principal == null ? null : principal.id();
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                "price"
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserProductPreviewDto> products = productFacade.getProductsByFilters(body, pageable, userId);

        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/liked")
    public ResponseEntity<List<LikedProductDto>> getLikedProducts(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<LikedProductDto> products = productFacade.getLikedProducts(principal.id());

        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        productFacade.toggleLike(id, principal.id());

        return ResponseEntity.ok().build();
    }

}

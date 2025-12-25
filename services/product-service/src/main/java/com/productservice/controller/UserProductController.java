package com.productservice.controller;

import com.productservice.security.CustomPrincipal;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
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
@RequiredArgsConstructor
@RequestMapping("/user/products")
@PreAuthorize("hasRole('USER')")
public class UserProductController {
    private final ProductFacade productFacade;


    @GetMapping("/search")
    public ResponseEntity<Page<ProductUserPreviewDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            UserProductParams body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                "price"
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductUserPreviewDto> products = productFacade.getProductsByFilters(body, pageable, principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/liked")
    public ResponseEntity<List<LikedProductDto>> getLikedProducts(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<LikedProductDto> products = productFacade.getLikedProducts(principal.getId());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProductDto> getUserProduct(
            @PathVariable long id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UserProductDto product = productFacade.getProduct(id, principal.getId());

        return ResponseEntity.ok(product);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        productFacade.toggleLike(id, principal.id());

        return ResponseEntity.ok().build();
    }
}

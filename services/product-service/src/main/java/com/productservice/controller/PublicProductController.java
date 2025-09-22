package com.productservice.controller;

import com.productservice.config.security.model.CustomPrincipal;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
import com.productservice.enums.SortDirectionEnum;
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
    public ResponseEntity<Page<ProductUserPreviewDto>> getPageOfProductsByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") SortDirectionEnum sortDirection,
            @ModelAttribute UserProductParams body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Sort sort = Sort.by(
                sortDirection.toSpringDirection(),
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
}

package com.productservice.controller;


import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
import com.productservice.services.facades.PublicProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/public/products")
@RequiredArgsConstructor
public class PublicProductController {
    private final PublicProductFacade publicProductFacade;

    @GetMapping("/{id}")
    public ResponseEntity<UserProductDto> getProduct(
            @PathVariable long id
    ) {
        UserProductDto product = publicProductFacade.getProduct(id);

        return ResponseEntity.ok(product);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<ProductUserPreviewDto>> getProductsByFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            UserProductParams body
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                "price"
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductUserPreviewDto> products = publicProductFacade.getProductsByFilters(body, pageable);

        return ResponseEntity.ok(products);
    }
}

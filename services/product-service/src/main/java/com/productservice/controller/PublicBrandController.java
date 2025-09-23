package com.productservice.controller;

import com.productservice.entity.Brand;
import com.productservice.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/brands")
@RequiredArgsConstructor
public class PublicBrandController {
    private final BrandService brandService;


    @GetMapping("/search")
    public ResponseEntity<Page<Brand>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String name
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brands = brandService.getAllActiveBrands(name, pageable);

        return ResponseEntity.ok(brands);
    }
}

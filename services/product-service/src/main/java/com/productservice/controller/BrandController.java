package com.productservice.controller;

import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.services.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Brand>> getAllBrands() {
        List<Brand> brands = brandService.getAllBrands();

        return ResponseEntity.ok(brands);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> createBrand(
            @RequestBody @Valid CreateBrandRequest body
    ) {
        Brand brand = brandService.create(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> updateBrand(
            @PathVariable int id,
            @RequestBody @Valid UpdateBrandRequest body
    ) {
        Brand brand = brandService.updateBrand(id, body);

        return ResponseEntity.ok(brand);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> deleteBrand(
            @PathVariable int id
    ) {
        brandService.delete(id);

        return ResponseEntity.ok().build();
    }
}

package com.productservice.controller;

import com.productservice.dto.request.AdminBrandParams;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.services.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBrandController {
    private final BrandService brandService;

    @GetMapping("/search")
    public ResponseEntity<Page<Brand>> getAllBrands(
            @ModelAttribute AdminBrandParams params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brands = brandService.getAllBrands(params, pageable);

        return ResponseEntity.ok().body(brands);
    }

    @PostMapping
    public ResponseEntity<Brand> createBrand(
            @RequestBody @Valid CreateBrandRequest body
    ) {
        Brand brand = brandService.create(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(
            @PathVariable int id,
            @RequestBody @Valid UpdateBrandRequest body
    ) {
        Brand brand = brandService.updateBrand(id, body);

        return ResponseEntity.ok(brand);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateBrand(
            @PathVariable int id
    ) {
        brandService.changeIsActive(id, false);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> activateBrand(
            @PathVariable int id
    ) {
        brandService.changeIsActive(id, true);

        return ResponseEntity.ok().build();
    }
}

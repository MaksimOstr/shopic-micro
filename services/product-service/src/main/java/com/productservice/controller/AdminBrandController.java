package com.productservice.controller;

import com.productservice.dto.request.AdminBrandParams;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.dto.response.BrandDeactivationCheckResponse;
import com.productservice.dto.response.BrandDeactivationResponse;
import com.productservice.entity.Brand;
import com.productservice.enums.BrandAdminSortByEnum;
import com.productservice.services.BrandService;
import com.productservice.services.BrandStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final BrandStatusService brandStatusService;

    @GetMapping("/search")
    public ResponseEntity<Page<Brand>> getAll(
            AdminBrandParams params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ID") BrandAdminSortByEnum sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Brand> brands = brandService.getAllBrands(params, pageable);

        return ResponseEntity.ok().body(brands);
    }

    @PostMapping
    public ResponseEntity<Brand> create(
            @RequestBody @Valid CreateBrandRequest body
    ) {
        Brand brand = brandService.create(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Brand> update(
            @PathVariable int id,
            @RequestBody @Valid UpdateBrandRequest body
    ) {
        Brand brand = brandService.updateBrand(id, body);

        return ResponseEntity.ok(brand);
    }

    @PatchMapping("/{id}/deactivation-check")
    public ResponseEntity<BrandDeactivationCheckResponse> deactivatingCheck(
            @PathVariable int id
    ) {
        BrandDeactivationCheckResponse response = brandStatusService.deactivationCheck(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<BrandDeactivationResponse> deactivate(
            @PathVariable int id
    ) {
        BrandDeactivationResponse response = brandStatusService.deactivate(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable int id
    ) {
        brandStatusService.activate(id);

        return ResponseEntity.ok().build();
    }
}

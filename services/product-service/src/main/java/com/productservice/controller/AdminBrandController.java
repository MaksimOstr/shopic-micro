package com.productservice.controller;

import com.productservice.dto.AdminBrandDto;
import com.productservice.dto.request.AdminBrandParams;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.enums.BrandAdminSortByEnum;
import com.productservice.services.BrandService;
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

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<Page<AdminBrandDto>> getAll(
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
        Page<AdminBrandDto> brands = brandService.searchAdminBrands(params, pageable);

        return ResponseEntity.ok().body(brands);
    }

    @PostMapping
    public ResponseEntity<Brand> create(
            @RequestBody @Valid CreateBrandRequest body
    ) {
        Brand brand = brandService.create(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminBrandDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateBrandRequest body
    ) {
        AdminBrandDto brand = brandService.updateBrand(id, body);

        return ResponseEntity.ok(brand);
    }

}

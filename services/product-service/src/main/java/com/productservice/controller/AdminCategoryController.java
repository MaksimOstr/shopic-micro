package com.productservice.controller;

import com.productservice.dto.AdminCategoryDto;
import com.productservice.dto.request.AdminCategoryParams;
import com.productservice.dto.request.CreateCategoryRequest;
import com.productservice.dto.request.UpdateCategoryRequest;
import com.productservice.entity.Category;
import com.productservice.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<AdminCategoryDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            AdminCategoryParams params
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminCategoryDto> categories = categoryService.searchAdminCategories(pageable, params);

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<Category> create(
            @RequestBody @Valid CreateCategoryRequest createCategoryRequest
    ) {
        Category category = categoryService.create(createCategoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminCategoryDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryRequest body
    ) {
        AdminCategoryDto category = categoryService.update(id, body);

        return ResponseEntity.ok(category);
    }
}












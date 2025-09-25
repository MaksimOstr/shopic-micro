package com.productservice.controller;

import com.productservice.dto.request.AdminCategoryParams;
import com.productservice.dto.request.CreateCategoryRequest;
import com.productservice.dto.request.UpdateCategoryRequest;
import com.productservice.dto.response.CategoryDeactivationCheckResponse;
import com.productservice.dto.response.CategoryDeactivationResponse;
import com.productservice.entity.Category;
import com.productservice.enums.CategoryAdminSortByEnum;
import com.productservice.services.CategoryService;
import com.productservice.services.CategoryStatusService;
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
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;
    private final CategoryStatusService categoryStatusService;

    @GetMapping("/search")
    public ResponseEntity<Page<Category>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ID") CategoryAdminSortByEnum sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            AdminCategoryParams params
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categories = categoryService.findAll(pageable, params);

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
    public ResponseEntity<Category> update(
            @PathVariable int id,
            @RequestBody @Valid UpdateCategoryRequest body
    ) {
        Category category = categoryService.update(id, body);

        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{id}/deactivation-check")
    public ResponseEntity<CategoryDeactivationCheckResponse> deactivationCheck(
            @PathVariable int id
    ) {
        CategoryDeactivationCheckResponse response = categoryStatusService.deactivationCheck(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CategoryDeactivationResponse> deactivate(
            @PathVariable int id
    ) {
        CategoryDeactivationResponse response = categoryStatusService.deactivate(id);

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable int id
    ) {
        categoryStatusService.activate(id);

        return ResponseEntity.ok().build();
    }
}

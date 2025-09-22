package com.productservice.controller;

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


@RestController
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<Category>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute AdminCategoryParams params
    ) {
        Pageable pageable = PageRequest.of(page, size);
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

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable int id
    ) {
        categoryService.changeIsActive(id, false);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> activate(
            @PathVariable int id
    ) {
        categoryService.changeIsActive(id, true);

        return ResponseEntity.ok().build();
    }
}

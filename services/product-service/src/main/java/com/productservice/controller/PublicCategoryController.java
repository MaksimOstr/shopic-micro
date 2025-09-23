package com.productservice.controller;

import com.productservice.entity.Category;
import com.productservice.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;


    @GetMapping("/search")
    public ResponseEntity<List<Category>> getAll(
            @RequestParam String name
    ) {
        List<Category> categories = categoryService.findAllActive(name);

        return ResponseEntity.ok(categories);
    }

}

package com.productservice.controller;

import com.productservice.dto.UserCategoryDto;
import com.productservice.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    @GetMapping
    public ResponseEntity<List<UserCategoryDto>> getAll(
            @RequestParam(required = false) String name
    ) {
        List<UserCategoryDto> categories = categoryService.searchUserCategories(name);

        return ResponseEntity.ok(categories);
    }

}

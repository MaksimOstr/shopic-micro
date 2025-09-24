package com.productservice.controller;

import com.productservice.dto.UserCategoryDto;
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
    public ResponseEntity<List<UserCategoryDto>> getAll(
            @RequestParam String name
    ) {
        List<UserCategoryDto> categories = categoryService.getUserCategoryDtoList(name);

        return ResponseEntity.ok(categories);
    }

}

package com.productservice.controller;

import com.productservice.dto.UserCategoryDto;
import com.productservice.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(
            summary = "Search brands",
            description = "Returns a page of brands"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Found brands"
            )
    })
    @GetMapping
    public ResponseEntity<List<UserCategoryDto>> getAll(
            @RequestParam(required = false) String name
    ) {
        List<UserCategoryDto> categories = categoryService.searchUserCategories(name);

        return ResponseEntity.ok(categories);
    }

}

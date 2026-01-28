package com.productservice.controller;

import com.productservice.dto.AdminBrandDto;
import com.productservice.dto.AdminCategoryDto;
import com.productservice.dto.ErrorResponseDto;
import com.productservice.dto.request.AdminCategoryParams;
import com.productservice.dto.request.CreateCategoryRequest;
import com.productservice.dto.request.UpdateCategoryRequest;
import com.productservice.entity.Category;
import com.productservice.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Search categories",
            description = "Returns a page of categories"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Found categories"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
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

    @Operation(
            summary = "Create new category",
            description = "Creates new category and returns its dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminCategoryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid input data",
                                            value = """
                                                    {
                                                        "isActive": "must not be provided"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category with the same name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @PostMapping
    public ResponseEntity<AdminCategoryDto> create(
            @RequestBody @Valid CreateCategoryRequest createCategoryRequest
    ) {
        AdminCategoryDto category = categoryService.create(createCategoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @Operation(
            summary = "Update category",
            description = "Updates category and returns updated brand dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminCategoryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid input data",
                                            value = """
                                                    {
                                                        "isActive": "must not be provided"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category with provided id was not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category with the same name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AdminCategoryDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryRequest body
    ) {
        AdminCategoryDto category = categoryService.update(id, body);

        return ResponseEntity.ok(category);
    }
}












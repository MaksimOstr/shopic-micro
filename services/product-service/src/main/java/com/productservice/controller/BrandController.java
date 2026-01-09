package com.productservice.controller;

import com.productservice.dto.ErrorResponseDto;
import com.productservice.dto.UserBrandDto;
import com.productservice.services.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

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
    public ResponseEntity<Page<UserBrandDto>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserBrandDto> brands = brandService.searchUserBrands(name, pageable);

        return ResponseEntity.ok(brands);
    }
}

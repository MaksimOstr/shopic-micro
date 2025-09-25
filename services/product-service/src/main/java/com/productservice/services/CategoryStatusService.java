package com.productservice.services;

import com.productservice.dto.response.CategoryDeactivationCheckResponse;
import com.productservice.dto.response.CategoryDeactivationResponse;
import com.productservice.entity.Category;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.CategoryRepository;
import com.productservice.services.products.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryStatusService {
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final CategoryService categoryService;

    @Transactional
    public CategoryDeactivationCheckResponse deactivationCheck(int id) {
        Category category = categoryService.getCategoryById(id);
        int activeCount = productService.countProductsByCategoryIdAndStatus(category.getId(), ProductStatusEnum.ACTIVE);
        String message = "During the deactivation, " + activeCount + " products will be affected";

        return new CategoryDeactivationCheckResponse(
                category.getId(),
                category.getName(),
                activeCount,
                message
        );
    }

    @Transactional
    public CategoryDeactivationResponse deactivate(int id) {
        Category category = categoryService.getCategoryById(id);

        if(!category.isActive()) {
            throw new IllegalStateException("Category is already deactivated");
        }

        category.setActive(false);

        int deactivatedCount = productService.deactivateByCategoryId(id);
        String message = deactivatedCount + " products were deactivated";

        return new CategoryDeactivationResponse(
                category.getId(),
                category.getName(),
                deactivatedCount,
                message
        );
    }

    public void activate(int id) {
        int updated = categoryRepository.changeIsActive(id, true);

        if(updated == 0) {
            throw new NotFoundException("Category not found");
        }
    }
}

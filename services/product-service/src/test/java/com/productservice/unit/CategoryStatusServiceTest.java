package com.productservice.unit;

import com.productservice.dto.response.CategoryDeactivationCheckResponse;
import com.productservice.dto.response.CategoryDeactivationResponse;
import com.productservice.entity.Category;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.services.CategoryService;
import com.productservice.services.CategoryStatusService;
import com.productservice.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryStatusServiceTest {
    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CategoryStatusService categoryStatusService;


    private static final int CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "CategoryTestName";
    private static final int DEACTIVATED_PRODUCTS_COUNT = 1;


    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .build();
    }


    @Test
    public void testDeactivationCheck_whenCalledWithNotExistingCategory_thenThrowException() {
        when(categoryService.getCategoryById(anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            categoryStatusService.deactivationCheck(CATEGORY_ID);
        });

        verify(categoryService).getCategoryById(CATEGORY_ID);
        verifyNoInteractions(productService);
    }

    @Test
    public void testDeactivationCheck_whenCalledWithExistingCategory_thenReturnResult() {
        when(categoryService.getCategoryById(anyInt())).thenReturn(category);
        when(productService.countProductsByCategoryIdAndStatus(anyInt(), any(ProductStatusEnum.class))).thenReturn(DEACTIVATED_PRODUCTS_COUNT);

        CategoryDeactivationCheckResponse result = categoryStatusService.deactivationCheck(CATEGORY_ID);

        verify(categoryService).getCategoryById(CATEGORY_ID);
        verify(productService).countProductsByCategoryIdAndStatus(CATEGORY_ID, ProductStatusEnum.ACTIVE);

        assertEquals(CATEGORY_ID, result.categoryId());
        assertEquals(CATEGORY_NAME, result.categoryName());
        assertEquals(DEACTIVATED_PRODUCTS_COUNT, result.activeProductsCount());
    }

    @Test
    public void testDeactivate_whenCalledWithDeactivatedCategory_thenThrowException() {
        category.setActive(false);

        when(categoryService.getCategoryById(anyInt())).thenReturn(category);

        assertThrows(IllegalStateException.class, () -> {
            categoryStatusService.deactivate(CATEGORY_ID);
        });

        verify(categoryService).getCategoryById(CATEGORY_ID);
        verifyNoInteractions(productService);
    }

    @Test
    public void testDeactivate_whenCalledWithNotExistingCategoryEntity_thenThrowException() {
        when(categoryService.getCategoryById(anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            categoryStatusService.deactivate(CATEGORY_ID);
        });

        verify(categoryService).getCategoryById(CATEGORY_ID);
        verifyNoInteractions(productService);
    }

    @Test
    public void testDeactivate_whenCalledWithActiveCategory_thenDeactivate() {
        category.setActive(true);

        when(categoryService.getCategoryById(anyInt())).thenReturn(category);
        when(productService.deactivateByCategoryId(anyInt())).thenReturn(DEACTIVATED_PRODUCTS_COUNT);

        CategoryDeactivationResponse result = categoryStatusService.deactivate(CATEGORY_ID);

        verify(categoryService).getCategoryById(CATEGORY_ID);
        verify(productService).deactivateByCategoryId(CATEGORY_ID);

        assertFalse(category.isActive());
        assertEquals(CATEGORY_ID, result.categoryId());
        assertEquals(CATEGORY_NAME, result.categoryName());
        assertEquals(DEACTIVATED_PRODUCTS_COUNT, result.deactivatedProductCount());
    }
}

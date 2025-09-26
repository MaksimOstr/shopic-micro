package com.productservice.unit;

import com.productservice.dto.request.CreateCategoryRequest;
import com.productservice.dto.request.UpdateCategoryRequest;
import com.productservice.entity.Category;
import com.productservice.exceptions.AlreadyExistsException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.CategoryRepository;
import com.productservice.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;


    private static final String REQUESTED_CATEGORY_NAME = "requestedCategoryName";
    private static final boolean REQUESTED_ACTIVE_STATUS = true;
    private static final String REQUESTED_DESCRIPTION = "requestedDescription";
    private static final String CATEGORY_NAME = "categoryName";
    private static final String CATEGORY_DESCRIPTION = "categoryDescription";
    private static final int CATEGORY_ID = 1;
    private static final CreateCategoryRequest CREATE_CATEGORY_REQUEST = new CreateCategoryRequest(
            REQUESTED_CATEGORY_NAME,
            REQUESTED_ACTIVE_STATUS,
            REQUESTED_DESCRIPTION
    );
    private static final UpdateCategoryRequest UPDATE_CATEGORY_REQUEST = new UpdateCategoryRequest(
            REQUESTED_CATEGORY_NAME,
            REQUESTED_DESCRIPTION
    );


    private Category category;

    @BeforeEach
    public void setup() {
        category = Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .build();
    }


    @Test
    public void testCreate_whenCalledWithExistingCategory_thenThrowException() {
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> {
            categoryService.create(CREATE_CATEGORY_REQUEST);
        });

        verify(categoryRepository).existsByName(REQUESTED_CATEGORY_NAME);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    public void testCreate_whenCalledWithNewCategory_thenCreateNewCategory() {
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.create(CREATE_CATEGORY_REQUEST);

        verify(categoryRepository).existsByName(REQUESTED_CATEGORY_NAME);
        verify(categoryRepository).save(categoryArgumentCaptor.capture());

        Category capturedCategory = categoryArgumentCaptor.getValue();

        assertEquals(REQUESTED_CATEGORY_NAME, capturedCategory.getName());
        assertEquals(REQUESTED_ACTIVE_STATUS, capturedCategory.isActive());
        assertEquals(REQUESTED_DESCRIPTION, capturedCategory.getDescription());
        assertEquals(category, result);
    }
    
    @Test
    public void testUpdate_whenCalledWithExistingCategoryName_thenThrowException() {
        when(categoryRepository.existsByName(anyString())).thenReturn(true);
        
        assertThrows(AlreadyExistsException.class, () -> {
            categoryService.update(CATEGORY_ID, UPDATE_CATEGORY_REQUEST);
        });

        verify(categoryRepository).existsByName(REQUESTED_CATEGORY_NAME);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    public void testUpdate_whenCalledWithNotExistingCategoryEntity_thenThrowException() {
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            categoryService.update(CATEGORY_ID, UPDATE_CATEGORY_REQUEST);
        });

        verify(categoryRepository).existsByName(REQUESTED_CATEGORY_NAME);
        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    public void testUpdate_whenCalledWithNewCategoryName_thenUpdateExistingCategory() {
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        Category result = categoryService.update(CATEGORY_ID, UPDATE_CATEGORY_REQUEST);

        verify(categoryRepository).existsByName(REQUESTED_CATEGORY_NAME);
        verify(categoryRepository).findById(CATEGORY_ID);

        assertEquals(REQUESTED_CATEGORY_NAME, result.getName());
        assertEquals(REQUESTED_DESCRIPTION, result.getDescription());
    }
}

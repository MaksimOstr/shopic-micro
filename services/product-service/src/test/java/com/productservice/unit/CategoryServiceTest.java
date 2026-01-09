package com.productservice.unit;

import com.productservice.dto.AdminCategoryDto;
import com.productservice.dto.UserCategoryDto;
import com.productservice.dto.request.AdminCategoryParams;
import com.productservice.dto.request.CreateCategoryRequest;
import com.productservice.dto.request.UpdateCategoryRequest;
import com.productservice.entity.Category;
import com.productservice.exceptions.ApiException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.CategoryMapper;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = Category.builder()
                .id(categoryId)
                .name("Electronics")
                .description("Desc")
                .isActive(true)
                .build();
    }

    @Test
    void create_shouldSaveCategory_whenNameDoesNotExist() {
        CreateCategoryRequest request = new CreateCategoryRequest("Books", true, "Books desc");
        AdminCategoryDto expectedDto = new AdminCategoryDto(UUID.randomUUID(), "Books", "Books desc", true);

        when(categoryRepository.existsByName(request.name())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(categoryMapper.toAdminCategoryDto(any(Category.class))).thenReturn(expectedDto);

        AdminCategoryDto result = categoryService.create(request);

        assertThat(result).isEqualTo(expectedDto);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());
        Category savedCategory = categoryCaptor.getValue();

        assertThat(savedCategory.getName()).isEqualTo(request.name());
        assertThat(savedCategory.getDescription()).isEqualTo(request.description());
        assertThat(savedCategory.isActive()).isTrue();
    }

    @Test
    void create_shouldThrowException_whenNameExists() {
        CreateCategoryRequest request =
                new CreateCategoryRequest("Books", true, "Books desc");

        when(categoryRepository.existsByName(request.name())).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Category name already exists");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateCategory_whenNameIsUnique() {
        UpdateCategoryRequest request =
                new UpdateCategoryRequest("New name", "New desc", false);

        when(categoryRepository.existsByNameAndIdNot(request.name(), categoryId))
                .thenReturn(false);
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        AdminCategoryDto dto = new AdminCategoryDto(
                categoryId,
                "New name",
                "New desc",
                false
        );

        when(categoryMapper.toAdminCategoryDto(category)).thenReturn(dto);

        AdminCategoryDto result = categoryService.update(categoryId, request);

        assertThat(result).isEqualTo(dto);
        assertThat(category.getName()).isEqualTo("New name");
        assertThat(category.getDescription()).isEqualTo("New desc");
        assertThat(category.isActive()).isFalse();
    }

    @Test
    void update_shouldThrowException_whenNameExists() {
        UpdateCategoryRequest request =
                new UpdateCategoryRequest("Books", "Desc", true);

        when(categoryRepository.existsByNameAndIdNot(request.name(), categoryId))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(categoryId, request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Category already exists");
    }

    @Test
    void getCategoryById_shouldReturnCategory_whenExists() {
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(categoryId);

        assertThat(result).isEqualTo(category);
    }

    @Test
    void getCategoryById_shouldThrowException_whenNotFound() {
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");
    }
}

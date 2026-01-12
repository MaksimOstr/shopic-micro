package com.productservice.services;

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
import com.productservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.productservice.utils.SpecificationUtils.equalsField;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public AdminCategoryDto create(CreateCategoryRequest dto) {
        if(existsByName(dto.name())) {
            throw new ApiException("Category name already exists", HttpStatus.CONFLICT);
        }

        Category category = Category.builder()
                .name(dto.name())
                .description(dto.description())
                .isActive(dto.isActive())
                .build();

        categoryRepository.save(category);

        return categoryMapper.toAdminCategoryDto(category);
    }

    @Transactional
    @CacheEvict(value = "categories", key = "#categoryId")
    public AdminCategoryDto update(UUID categoryId, UpdateCategoryRequest dto) {
        if(categoryRepository.existsByNameAndIdNot(dto.name(), categoryId)) {
            throw new ApiException("Category already exists", HttpStatus.CONFLICT);
        }

        Category category = getCategoryById(categoryId);

        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setActive(dto.isActive());

        return categoryMapper.toAdminCategoryDto(category);
    }

    @Cacheable(value = "categories", key = "#id")
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public Page<AdminCategoryDto> searchAdminCategories(Pageable pageable, AdminCategoryParams params) {
        Specification<Category> spec = SpecificationUtils.<Category>iLike("name", params.name())
                .and(equalsField("isActive", params.isActive()));
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        List<AdminCategoryDto> categoryDtoList = categoryMapper.toAdminCategoryDtoList(categoryPage.getContent());

        return new PageImpl<>(categoryDtoList, pageable, categoryPage.getTotalElements());
    }

    public List<UserCategoryDto> searchUserCategories(String name) {
        Specification<Category> spec = SpecificationUtils.<Category>iLike("name", name)
                .and(equalsField("isActive", true));
        List<Category> categoryList = categoryRepository.findAll(spec);

        return categoryMapper.toUserCategoryDtoList(categoryList);
    }

    private boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}

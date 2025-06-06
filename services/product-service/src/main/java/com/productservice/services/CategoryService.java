package com.productservice.services;

import com.productservice.dto.request.CreateCategoryRequest;
import com.productservice.dto.request.UpdateCategoryRequest;
import com.productservice.entity.Category;
import com.productservice.exceptions.AlreadyExistsException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category create(CreateCategoryRequest dto) {
        if(existsByName(dto.name())) {
            throw new AlreadyExistsException("Category name already exists");
        }

        Category category = new Category(
                dto.name(),
                dto.description()
        );

        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(int categoryId, UpdateCategoryRequest dto) {
        Category category = findById(categoryId);

        Optional.ofNullable(dto.name()).ifPresent(category::setName);
        Optional.ofNullable(dto.description()).ifPresent(category::setDescription);

        return category;
    }

    public Category findById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    private boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}

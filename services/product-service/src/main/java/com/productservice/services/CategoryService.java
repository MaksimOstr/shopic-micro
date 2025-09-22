package com.productservice.services;

import com.productservice.dto.request.AdminCategoryParams;
import com.productservice.dto.request.CreateCategoryRequest;
import com.productservice.dto.request.UpdateCategoryRequest;
import com.productservice.entity.Category;
import com.productservice.exceptions.AlreadyExistsException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.CategoryRepository;
import com.productservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.productservice.utils.SpecificationUtils.equalsBoolean;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
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

    public Page<Category> findAll(Pageable pageable, AdminCategoryParams params) {
        Specification<Category> spec = SpecificationUtils.<Category>iLike("name", params.name())
                .and(equalsBoolean("isActive", params.isActive()));

        return categoryRepository.findAll(spec, pageable);
    }

    public List<Category> findAllActive(String name) {
        Specification<Category> spec = SpecificationUtils.<Category>iLike("name", name)
                .and(equalsBoolean("isActive", true));

        return categoryRepository.findAll(spec);
    }



    public void changeIsActive(int id, boolean active) {
        int updated = categoryRepository.changeIsActive(id, active);

        if(updated == 0) {
            throw new NotFoundException("Category not found");
        }
    }

    private boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}

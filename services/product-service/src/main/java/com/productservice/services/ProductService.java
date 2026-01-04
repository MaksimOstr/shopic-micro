package com.productservice.services;

import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.productservice.utils.ProductUtils.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;

    @Transactional
    public Product create(CreateProductRequest dto, String imageUrl) {
        Brand brand = Optional.ofNullable(dto.brandId())
                .map(brandService::getBrandById)
                .orElse(null);
        Category category = categoryService.getCategoryById(dto.categoryId());
        Product product = createProductEntity(dto, imageUrl, category, brand);

        return productRepository.save(product);
    }

    public Product getActiveWithCategoryAndBrandById(UUID id) {
        return productRepository.findActiveWithCategoryAndBrandById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public List<Product> getActiveProductsByIds(Collection<UUID> idList) {
        return productRepository.findAllActiveByIdList(idList);
    }

    public List<Product> getActiveProductsByIdsWithLock(Collection<UUID> idList) {
        return productRepository.findActiveByIdInWithLock(idList);
    }

    @Transactional
    public Product updateProduct(UUID id, UpdateProductRequest dto) {
        Product product = getProductById(id);

        Optional.ofNullable(dto.name()).ifPresent(product::setName);
        Optional.ofNullable(dto.deleted()).ifPresent(product::setDeleted);
        Optional.ofNullable(dto.description()).ifPresent(product::setDescription);
        Optional.ofNullable(dto.price()).ifPresent(product::setPrice);
        Optional.ofNullable(dto.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(dto.categoryId()).ifPresent(categoryId -> {
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);
        });
        Optional.ofNullable(dto.brandId()).ifPresent(brandId -> {
            Brand brand = brandService.getBrandById(brandId);
            product.setBrand(brand);
        });

        return productRepository.save(product);
    }

    public String updateProductImage(UUID id, String newImageUrl) {
        Product product = getProductById(id);
        String oldImageUrl = product.getImageUrl();
        product.setImageUrl(newImageUrl);
        productRepository.save(product);

        return oldImageUrl;
    }

    public boolean existsById(UUID id) {
        return productRepository.existsById(id);
    }

    public Page<Product> getProductPageBySpec(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }


    private Product createProductEntity(CreateProductRequest dto, String url, Category category, Brand brand) {
        return Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .isDeleted(dto.isDeleted())
                .imageUrl(url)
                .category(category)
                .stockQuantity(dto.stockQuantity())
                .brand(brand)
                .build();
    }
}

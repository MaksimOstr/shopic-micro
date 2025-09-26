package com.productservice.services;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductBasicInfoDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.productservice.utils.ProductUtils.PRODUCT_NOT_FOUND;
import static com.productservice.utils.Utils.getUUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product create(CreateProductRequest dto, String imageUrl, Category category, Brand brand) {
        Product product = createProductEntity(dto, imageUrl, category, brand);

        return productRepository.save(product);
    }

    @Transactional
    public int deactivateByBrandId(int brandId) {
        return productRepository.deactivateAllActiveProductsByBrandId(brandId);
    }

    @Transactional
    public int deactivateByCategoryId(int categoryId) {
        return productRepository.deactivateAllActiveProductsByCategoryId(categoryId);
    }

    @Transactional
    public void activateProduct(long id) {
        Product product = getProductWithCategoryAndBrand(id);
        Brand brand = product.getBrand();
        Category category = product.getCategory();

        if(product.getStatus().equals(ProductStatusEnum.ACTIVE)) {
            throw new IllegalStateException("Product is already activated");
        }

        if (!brand.isActive()) {
            throw new IllegalStateException("Cannot activate product: brand " + brand.getName() + " is inactive");
        }

        if (!category.isActive()) {
            throw new IllegalStateException("Cannot activate product: category " + category.getName() + " is inactive");
        }

        product.setStatus(ProductStatusEnum.ACTIVE);
    }

    @Transactional
    public void archiveProduct(long id) {
        Product product = getProductById(id);

        if(product.getStatus().equals(ProductStatusEnum.ARCHIVED)) {
            throw new IllegalStateException("Product is already archived");
        }

        product.setStatus(ProductStatusEnum.ARCHIVED);
    }

    public List<Product> getProductsForUpdate(List<Long> idList) {
        return productRepository.findProductsForUpdate(idList);
    }

    public ProductBasicInfoDto getActiveProductBasicInfo(long id) {
        return productRepository.findActiveProductBasicInfoById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public List<ProductBasicInfoDto> getActiveProductBasicInfoList(List<Long> idList) {
        return productRepository.findActiveProductsBasicInfoByIds(idList);
    }

    public AdminProductDto getAdminProductById(long id) {
        return productRepository.getAdminProduct(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public AdminProductDto getAdminProductBySku(UUID sku) {
        return productRepository.getAdminProduct(sku)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public boolean existsById(long id) {
        return productRepository.existsById(id);
    }

    public int countProductsByBrandIdAndStatus(int brandId, ProductStatusEnum status) {
        return productRepository.countByBrand_IdAndStatus(brandId, status);
    }

    public int countProductsByCategoryIdAndStatus(int brandId, ProductStatusEnum status) {
        return productRepository.countByCategory_IdAndStatus(brandId, status);
    }

    public UserProductDto getActiveUserProduct(long id) {
        return productRepository.getActiveUserProductById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Product getProductWithCategoryAndBrand(long id) {
        return productRepository.getProductWithCategoryAndBrand(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public List<LikedProductDto> getProductsByIds(Set<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    public Page<Product> getProductPageBySpec(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Optional<String> getOptionalProductImageUrl(long productId) {
        return productRepository.getProductImageUrl(productId);
    }

    private Product createProductEntity(CreateProductRequest dto, String url, Category category, Brand brand) {
        return Product.builder()
                .name(dto.name())
                .description(dto.description())
                .sku(getUUID())
                .price(dto.price())
                .imageUrl(url)
                .status(dto.status())
                .category(category)
                .stockQuantity(dto.stockQuantity())
                .brand(brand)
                .build();
    }
}

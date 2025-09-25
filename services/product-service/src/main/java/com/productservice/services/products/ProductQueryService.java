package com.productservice.services.products;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.UserProductDto;
import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.dto.ProductBasicInfoDto;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.productservice.utils.ProductUtils.PRODUCT_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class ProductQueryService {
    private final ProductRepository productRepository;

    public List<Product> getProductsForUpdate(List<Long> productIds) {
        return productRepository.findProductsForUpdate(productIds);
    }

    public ProductBasicInfoDto getActiveProductBasicInfo(long productId) {
        return productRepository.findActiveProductBasicInfoById(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public List<ProductBasicInfoDto> getActiveProductBasicInfoList(List<Long> productIds) {
        return productRepository.findActiveProductsBasicInfoByIds(productIds);
    }

    public AdminProductDto getAdminProduct(UUID sku) {
        return productRepository.getAdminProduct(sku)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public boolean isProductExist(long id) {
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

    public AdminProductDto getAdminProduct(long id) {
        return productRepository.getAdminProduct(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public List<LikedProductDto> getProductsByIds(Set<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    public Page<Product> getProductPageBySpec(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    public String getProductImageUrl(long productId) {
        return productRepository.getProductImageUrl(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }
}

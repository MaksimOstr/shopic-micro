package com.productservice.services.products;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.UserProductDto;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.projection.ProductDto;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductInfoDto;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public List<ProductInfoDto> getProductInfo(List<Long> productIds) {
        return productRepository.findProductPrices(productIds);
    }

    public Product getProductBySku(UUID sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public boolean isProductExist(long id) {
        return productRepository.existsById(id);
    }

    public UserProductDto getUserProductById(long id) {
        return productRepository.getUserProduct(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Product getProductWithCategoryAndBrand(long id) {
        return productRepository.getProductWithCategoryAndBrand(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public AdminProductDto getAdminProductById(long id) {
        return productRepository.getAdminProduct(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public ProductForCartDto getProductInfoForCart(long productId) {
        return productRepository.getProductForCartById(productId)
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

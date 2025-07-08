package com.productservice.services.products;

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

    public Product getEnabledProductById(long id) {
        return productRepository.getEnabledProduct(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public ProductForCartDto getProductInfoForCart(long productId) {
        return productRepository.getProductForCartById(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public List<ProductDto> getProductsByIds(Set<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    public Page<ProductDto> getProductPage(Pageable pageable) {
        return productRepository.getPageOfProducts(pageable);
    }

    public Page<Product> getProductPageBySpec(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    public String getProductImageUrl(long productId) {
        return productRepository.getProductImageUrl(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }
}

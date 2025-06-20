package com.productservice.services.products;

import com.productservice.dto.request.ProductParams;
import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProductFacade {
    private final ProductSearchService productSearchService;
    private final ProductQueryService productQueryService;

    public Page<ProductDto> getProductsByFilters(ProductParams params, Pageable pageable, long userId) {
        return productSearchService.findPublicProductsByFilters(params, pageable, userId);
    }

    public Page<ProductDto> getProductPage(Pageable pageable, long userId) {
        return productSearchService.getPageOfProducts(pageable, userId);
    }

    public List<ProductDto> getLikedProducts(long userId) {
        return productSearchService.getLikedProducts(userId);
    }

    public Product getProduct(long productId) {
        return productQueryService.getEnabledProductById(productId);
    }
}

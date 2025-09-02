package com.productservice.services.products;

import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.ProductParams;
import com.productservice.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.productservice.utils.SpecificationUtils.*;
import static com.productservice.utils.SpecificationUtils.gte;
import static com.productservice.utils.SpecificationUtils.hasChild;

@Service
@RequiredArgsConstructor
public class UserProductFacade {
    private final ProductSearchService productSearchService;
    private final ProductQueryService productQueryService;

    public Page<ProductUserPreviewDto> getProductsByFilters(ProductParams params, Pageable pageable, long userId) {
        Specification<Product> spec = iLike("name", params.getName())
                .and(hasActiveStatus("enabled", true))
                .and(lte("price", params.getToPrice()))
                .and(gte("price", params.getFromPrice()))
                .and(hasChild("category", params.getCategoryId()))
                .and(hasChild("brand", params.getBrandId()));

        return productSearchService.getPageOfUserProductsByFilters(spec, pageable, userId);
    }

    public List<LikedProductDto> getLikedProducts(long userId) {
        return productSearchService.getLikedProducts(userId);
    }

    public UserProductDto getProduct(long productId) {
        return productQueryService.getUserProductById(productId);
    }
}

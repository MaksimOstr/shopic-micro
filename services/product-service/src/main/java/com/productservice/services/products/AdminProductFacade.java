package com.productservice.services.products;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.EnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.productservice.utils.ProductUtils.buildAdminProductSpec;



@Service
@RequiredArgsConstructor
public class AdminProductFacade {
    private final ProductQueryService productQueryService;
    private final EnrichmentService enrichmentService;
    private final ProductMapper productMapper;


    public AdminProductDto getAdminProduct(long productId, long userId) {
        AdminProductDto product = productQueryService.getAdminProduct(productId);

        enrichmentService.enrichProductWithLike(product, userId);
        enrichmentService.enrichProductWithRating(product);

        return product;
    }

    public Page<ProductAdminPreviewDto> getProductsByFilters(AdminProductParams params, Pageable pageable, long userId) {
        Specification<Product> spec = buildAdminProductSpec(params);
        Page<Product> productPage = productQueryService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductAdminPreviewDto> previewDtoList = productMapper.productToProductAdminPreviewDtoList(productList);

        enrichmentService.enrichProductListWithLikes(previewDtoList, userId);
        enrichmentService.enrichProductListWithRatings(previewDtoList);

        return new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }
}

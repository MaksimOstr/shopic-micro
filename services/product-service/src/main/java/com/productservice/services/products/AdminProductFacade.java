package com.productservice.services.products;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.RatingEnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.productservice.utils.ProductUtils.buildAdminProductSpec;



@Service
@RequiredArgsConstructor
public class AdminProductFacade {
    private final ProductQueryService productQueryService;
    private final RatingEnrichmentService ratingEnrichmentService;
    private final ProductMapper productMapper;


    public AdminProductDto getAdminProduct(long productId) {
        AdminProductDto product = productQueryService.getAdminProduct(productId);

        ratingEnrichmentService.enrichProduct(product);

        return product;
    }

    public AdminProductDto getAdminProduct(UUID sku) {
        AdminProductDto product = productQueryService.getAdminProduct(sku);

        ratingEnrichmentService.enrichProduct(product);

        return product;
    }

    public Page<ProductAdminPreviewDto> getProductsByFilters(AdminProductParams params, Pageable pageable) {
        Specification<Product> spec = buildAdminProductSpec(params);
        Page<Product> productPage = productQueryService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductAdminPreviewDto> previewDtoList = productMapper.productToProductAdminPreviewDtoList(productList);

        ratingEnrichmentService.enrichProductList(previewDtoList);

        return new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }
}

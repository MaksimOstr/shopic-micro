package com.productservice.services.facades;

import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.RatingEnrichmentService;
import com.productservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.productservice.utils.ProductUtils.buildUserProductSpec;


@Service
@RequiredArgsConstructor
public class PublicProductFacade {
    private final ProductService productService;
    private final RatingEnrichmentService ratingEnrichmentService;
    private final ProductMapper productMapper;


    public UserProductDto getProduct(long productId) {
        UserProductDto product = productService.getActiveUserProduct(productId);

        ratingEnrichmentService.enrichProduct(product);

        return product;
    }

    public Page<ProductUserPreviewDto> getProductsByFilters(UserProductParams params, Pageable pageable) {
        Specification<Product> spec = buildUserProductSpec(params);
        Page<Product> productPage = productService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductUserPreviewDto> previewDtoList = productMapper.productToProductUserPreviewDtoList(productList);

        ratingEnrichmentService.enrichProductList(previewDtoList);

        return  new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }

}

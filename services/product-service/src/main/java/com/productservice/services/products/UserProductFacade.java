package com.productservice.services.products;

import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.EnrichmentService;
import com.productservice.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

import static com.productservice.utils.ProductUtils.buildUserProductSpec;


@Service
@RequiredArgsConstructor
public class UserProductFacade {
    private final ProductQueryService productQueryService;
    private final EnrichmentService enrichmentService;
    private final ProductMapper productMapper;
    private final LikeService likeService;

    public UserProductDto getProduct(long productId, long userId) {
        UserProductDto product = productQueryService.getActiveUserProduct(productId);

        enrichmentService.enrichProductWithLike(product, userId);
        enrichmentService.enrichProductWithRating(product);

        return product;
    }

    public List<LikedProductDto> getLikedProducts(long userId) {
        Set<Long> likedProducts = likeService.getLikedProductIds(userId);

        return productQueryService.getProductsByIds(likedProducts);
    }

    public Page<ProductUserPreviewDto> getProductsByFilters(UserProductParams params, Pageable pageable, long userId) {
        Specification<Product> spec = buildUserProductSpec(params);
        Page<Product> productPage = productQueryService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductUserPreviewDto> previewDtoList = productMapper.productToProductUserPreviewDtoList(productList);

        enrichmentService.enrichProductListWithLikes(previewDtoList, userId);
        enrichmentService.enrichProductListWithRatings(previewDtoList);

        return  new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }
}

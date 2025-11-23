package com.productservice.services.facades;

import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.LikeEnrichmentService;
import com.productservice.services.LikeService;
import com.productservice.services.ProductService;
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
    private final ProductService productService;
    private final LikeEnrichmentService likeEnrichmentService;
    private final ProductMapper productMapper;
    private final LikeService likeService;

    public UserProductDto getProduct(long productId, long userId) {
        UserProductDto product = productService.getActiveUserProduct(productId);

        likeEnrichmentService.enrichProduct(product, userId);

        return product;
    }

    public List<LikedProductDto> getLikedProducts(long userId) {
        Set<Long> likedProducts = likeService.getLikedProductIds(userId);

        return productService.getProductsByIds(likedProducts);
    }

    public void toggleLike(long productId, long userId) {
        likeService.toggleLike(productId, userId);
    }

    public Page<ProductUserPreviewDto> getProductsByFilters(UserProductParams params, Pageable pageable, long userId) {
        Specification<Product> spec = buildUserProductSpec(params);
        Page<Product> productPage = productService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductUserPreviewDto> previewDtoList = productMapper.productToProductUserPreviewDtoList(productList);

        likeEnrichmentService.enrichProductList(previewDtoList, userId);

        return  new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }
}

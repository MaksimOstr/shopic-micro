package com.productservice.services.products;

import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.services.LikeService;
import com.productservice.services.grpc.GrpcReviewService;
import com.productservice.utils.SpecificationUtils;
import com.shopic.grpc.reviewservice.ProductRatingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.productservice.utils.SpecificationUtils.*;
import static com.productservice.utils.SpecificationUtils.gte;
import static com.productservice.utils.SpecificationUtils.hasChild;


@Service
@RequiredArgsConstructor
public class UserProductFacade {
    private final ProductSearchService productSearchService;
    private final ProductQueryService productQueryService;
    private final GrpcReviewService grpcReviewService;
    private final LikeService likeService;

    public Page<ProductUserPreviewDto> getProductsByFilters(UserProductParams params, Pageable pageable, long userId) {
        Specification<Product> spec = SpecificationUtils.<Product>iLike("name", params.productName())
                .and(equalsEnum("status", ProductStatusEnum.ACTIVE))
                .and(lte("price", params.toPrice()))
                .and(gte("price", params.fromPrice()))
                .and(hasChild("category", params.categoryId()))
                .and(hasChild("brand", params.brandId()));

        return productSearchService.getPageOfUserProductsByFilters(spec, pageable, userId);
    }

    public List<LikedProductDto> getLikedProducts(long userId) {
        return productSearchService.getLikedProducts(userId);
    }

    public UserProductDto getProduct(long productId, long userId) {
        UserProductDto product = productQueryService.getActiveUserProduct(productId);
        ProductRatingResponse rating = grpcReviewService.getProductRating(productId);
        boolean isLiked = likeService.isProductLiked(productId, userId);

        product.setRating(new BigDecimal(rating.getRating()));
        product.setLiked(isLiked);
        product.setReviewCount(rating.getReviewCount());

        return product;
    }
}

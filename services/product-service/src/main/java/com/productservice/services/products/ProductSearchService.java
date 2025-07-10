package com.productservice.services.products;

import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.projection.ProductDto;
import com.productservice.services.LikeService;
import com.productservice.services.grpc.GrpcReviewService;
import com.shopic.grpc.reviewservice.ProductRating;
import com.shopic.grpc.reviewservice.ProductRatingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductQueryService productQueryService;
    private final ProductMapper productMapper;
    private final LikeService likeService;
    private final GrpcReviewService grpcReviewService;


    public List<ProductDto> getLikedProducts(long userId) {
        Set<Long> likedProducts = likeService.getLikedProductIds(userId);

        return productQueryService.getProductsByIds(likedProducts);
    }

    public Page<ProductDto> getPageOfProductsByFilters(Specification<Product> spec, Pageable pageable, long userId) {
        Page<Product> productPage = productQueryService.getProductPageBySpec(spec, pageable);
        List<Product> products = productPage.getContent();
        List<ProductDto> productDtoList = products.stream().map(productMapper::productToProductDto).toList();

        markLikedProducts(productDtoList, userId);
        setProductRatings(productDtoList);

        return new PageImpl<>(productDtoList, pageable, productPage.getTotalElements());
    }


    private void markLikedProducts(List<ProductDto> products, long userId) {
        Set<Long> likesIds = likeService.getLikedProductIds(userId);

        for (ProductDto product : products) {
            product.setLiked(likesIds.contains(product.getId()));
        }
    }

    private void setProductRatings(List<ProductDto> products) {
        List<Long> productIdList = products.stream().map(ProductDto::getId).toList();
        ProductRatingsResponse response = grpcReviewService.getProductRatings(productIdList);
        Map<Long, ProductRating> productRatingMap = response.getProductRatingList().stream().collect(Collectors.toMap(ProductRating::getProductId, Function.identity()));

        for (ProductDto product : products) {
            ProductRating productRating = productRatingMap.get(product.getId());

            if (productRating != null) {
                product.setRating(new BigDecimal(productRating.getRating()));
                product.setReviewCount(productRating.getReviewCount());
            } else {
                product.setRating(BigDecimal.ZERO);
                product.setReviewCount(0);
            }
        }
    }
}

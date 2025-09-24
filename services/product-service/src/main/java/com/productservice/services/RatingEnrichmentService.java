package com.productservice.services;

import com.productservice.dto.BaseProductDto;
import com.productservice.dto.ProductPreviewDto;
import com.productservice.services.grpc.GrpcReviewService;
import com.shopic.grpc.reviewservice.ProductRating;
import com.shopic.grpc.reviewservice.ProductRatingResponse;
import com.shopic.grpc.reviewservice.ProductRatingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingEnrichmentService {
    private final GrpcReviewService grpcReviewService;

    public void enrichProduct(BaseProductDto product) {
        ProductRatingResponse rating = grpcReviewService.getProductRating(product.getId());

        product.setRating(new BigDecimal(rating.getRating()));
        product.setReviewCount(rating.getReviewCount());
    }

    public void enrichProductList(List<? extends ProductPreviewDto> products) {
        List<Long> productIdList = products.stream().map(ProductPreviewDto::getId).toList();
        ProductRatingsResponse response = grpcReviewService.getProductRatings(productIdList);
        Map<Long, ProductRating> productRatingMap = response.getProductRatingList().stream().collect(Collectors.toMap(ProductRating::getProductId, Function.identity()));

        for (ProductPreviewDto product : products) {
            ProductRating rating = productRatingMap.getOrDefault(
                    product.getId(),
                    ProductRating.newBuilder()
                            .setProductId(product.getId())
                            .setRating(String.valueOf(0))
                            .setReviewCount(0)
                            .build()
            );

            product.setRating(new BigDecimal(rating.getRating()));
            product.setReviewCount(rating.getReviewCount());
        }
    }
}

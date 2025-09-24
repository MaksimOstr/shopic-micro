package com.productservice.services;

import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikeEnrichmentService {
    private final LikeService likeService;


    public void enrichProduct(UserProductDto product, long userId) {
        boolean isLiked = likeService.isProductLiked(product.getId(), userId);

        product.setLiked(isLiked);
    }

    public void enrichProductList(List<ProductUserPreviewDto> products, long userId) {
        Set<Long> likesIds = likeService.getLikedProductIds(userId);

        for (ProductUserPreviewDto product : products) {
            product.setLiked(likesIds.contains(product.getId()));
        }
    }
}

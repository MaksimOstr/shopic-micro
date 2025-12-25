package com.productservice.services;

import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeEnrichmentService {
    private final LikeService likeService;


    public void enrichProduct(UserProductDto product, UUID userId) {
        boolean isLiked = likeService.isProductLiked(product.getId(), userId);

        product.setLiked(isLiked);
    }

    public void enrichProductList(List<ProductUserPreviewDto> products, UUID userId) {
        Set<Long> likesIds = likeService.getLikedProductIds(userId);

        for (ProductUserPreviewDto product : products) {
            product.setLiked(likesIds.contains(product.getId()));
        }
    }
}

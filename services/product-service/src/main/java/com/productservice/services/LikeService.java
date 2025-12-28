package com.productservice.services;

import com.productservice.entity.Like;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.LikeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public void toggleLike(Product product, UUID userId) {
        boolean isExists = existsByProductIdAndUserId(product.getId(), userId);
        if (isExists) {
            likeRepository.deleteByProductAndUserId(product, userId);
        } else {
            createLike(product, userId);
        }
    }

    private void createLike(Product product, UUID userId) {
        Like like = Like.builder()
                .userId(userId)
                .product(product)
                .build();

        likeRepository.save(like);
    }

    public Set<UUID> getLikedProductIds(UUID userId) {
        return likeRepository.findLikedProductIds(userId);
    }

    public boolean existsByProductIdAndUserId(UUID productId, UUID userId) {
        return likeRepository.existsByProduct_IdAndUserId(productId, userId);
    }
}

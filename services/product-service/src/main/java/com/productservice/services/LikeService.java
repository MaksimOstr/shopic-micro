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
    private final EntityManager entityManager;


    public void toggleLike(UUID productId, UUID userId) {
        boolean isExists = isProductLiked(productId, userId);
        if (isExists) {
            likeRepository.deleteByProduct_IdAndUserId(productId, userId);
        } else {
            createLike(productId, userId);
        }
    }

    private void createLike(UUID productId, UUID userId) {
        Like like = Like.builder()
                .userId(userId)
                .product(entityManager.getReference(Product.class, productId))
                .build();

        try {
            likeRepository.save(like);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Product does not exist");
        }
    }

    public Set<UUID> getLikedProductIds(UUID userId) {
        return likeRepository.findLikedProductIds(userId);
    }

    public boolean isProductLiked(UUID productId, UUID userId) {
        return likeRepository.existsByProduct_IdAndUserId(productId, userId);
    }
}

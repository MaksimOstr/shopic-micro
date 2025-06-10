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


@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final EntityManager entityManager;


    public void toggleLike(long productId, long userId) {
        boolean isExists = likeRepository.existsByProduct_IdAndUserId(productId, userId);
        if (isExists) {
            likeRepository.deleteByProduct_IdAndUserId(productId, userId);
        } else {
            createLike(productId, userId);
        }
    }

    public void createLike(long productId, long userId) {
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


    public Set<Long> getLikedProductIds(long userId) {
        return likeRepository.findLikedProductIds(userId);
    }

    public int getLikeCount(long productId) {
        return likeRepository.countByProduct_Id(productId);
    }
}

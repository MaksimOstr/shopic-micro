package com.productservice.unit;

import com.productservice.entity.Like;
import com.productservice.entity.Product;
import com.productservice.repository.LikeRepository;
import com.productservice.services.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    private UUID userId;
    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
    }

    @Test
    void toggleLike_shouldDeleteLike_whenLikeExists() {
        when(likeRepository.existsByProduct_IdAndUserId(productId, userId))
                .thenReturn(true);

        likeService.toggleLike(product, userId);

        verify(likeRepository).deleteByProductAndUserId(product, userId);
        verify(likeRepository, never()).save(any());
    }

    @Test
    void toggleLike_shouldCreateLike_whenLikeDoesNotExist() {
        when(likeRepository.existsByProduct_IdAndUserId(productId, userId))
                .thenReturn(false);

        likeService.toggleLike(product, userId);

        verify(likeRepository).save(any(Like.class));
        verify(likeRepository, never()).deleteByProductAndUserId(any(), any());
    }

    @Test
    void toggleLike_shouldCheckExistenceOnce() {
        when(likeRepository.existsByProduct_IdAndUserId(productId, userId))
                .thenReturn(false);

        likeService.toggleLike(product, userId);

        verify(likeRepository, times(1))
                .existsByProduct_IdAndUserId(productId, userId);
    }

    @Test
    void getLikedProductIds_shouldReturnSetFromRepository() {
        Set<UUID> likedProducts = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(likeRepository.findLikedProductIds(userId))
                .thenReturn(likedProducts);

        Set<UUID> result = likeService.getLikedProductIds(userId);

        assertThat(result).isEqualTo(likedProducts);
        verify(likeRepository).findLikedProductIds(userId);
    }

    @Test
    void getLikedProductIds_shouldReturnEmptySet_whenNoLikes() {
        when(likeRepository.findLikedProductIds(userId))
                .thenReturn(Set.of());

        Set<UUID> result = likeService.getLikedProductIds(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByProductIdAndUserId_shouldReturnTrue() {
        when(likeRepository.existsByProduct_IdAndUserId(productId, userId))
                .thenReturn(true);

        boolean result = likeService.existsByProductIdAndUserId(productId, userId);

        assertThat(result).isTrue();
        verify(likeRepository).existsByProduct_IdAndUserId(productId, userId);
    }

    @Test
    void existsByProductIdAndUserId_shouldReturnFalse() {
        when(likeRepository.existsByProduct_IdAndUserId(productId, userId))
                .thenReturn(false);

        boolean result = likeService.existsByProductIdAndUserId(productId, userId);

        assertThat(result).isFalse();
    }
}


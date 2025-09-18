package com.productservice.unit;

import com.productservice.entity.Like;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.LikeRepository;
import com.productservice.services.LikeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private LikeService likeService;

    private static final long USER_ID = 1L;
    private static final long PRODUCT_ID = 1L;

    private Product product;

    @BeforeEach
    public void setUp() {
        product = Product.builder()
                .id(PRODUCT_ID)
                .build();
    }

    @Test
    public void testToggleLike_whenCalledWithNotExistingLike_thenCreateNewLike() {
        ArgumentCaptor<Like> likeArgumentCaptor = ArgumentCaptor.forClass(Like.class);

        when(likeRepository.existsByProduct_IdAndUserId(anyLong(), anyLong())).thenReturn(false);
        when(entityManager.getReference(any(), anyLong())).thenReturn(product);

        likeService.toggleLike(PRODUCT_ID, USER_ID);

        verify(likeRepository).existsByProduct_IdAndUserId(PRODUCT_ID, USER_ID);
        verify(entityManager).getReference(Product.class, PRODUCT_ID);
        verify(likeRepository).save(likeArgumentCaptor.capture());
        verifyNoMoreInteractions(likeRepository);

        Like capturedLike = likeArgumentCaptor.getValue();

        assertEquals(USER_ID, capturedLike.getUserId());
        assertEquals(product, capturedLike.getProduct());
    }

    @Test
    public void testToggleLike_whenCalledWithExistingLike_thenRemoveLike() {
        when(likeRepository.existsByProduct_IdAndUserId(anyLong(), anyLong())).thenReturn(true);

        likeService.toggleLike(PRODUCT_ID, USER_ID);

        verify(likeRepository).existsByProduct_IdAndUserId(PRODUCT_ID, USER_ID);
        verify(likeRepository).deleteByProduct_IdAndUserId(PRODUCT_ID, USER_ID);
        verifyNoMoreInteractions(likeRepository);
        verifyNoInteractions(entityManager);
    }

    @Test
    public void testToggleLike_whenCalledWithNotExistingLikeButWasCreatedDuringCompletion_thenThrowException() {
        when(likeRepository.existsByProduct_IdAndUserId(anyLong(), anyLong())).thenReturn(false);
        when(entityManager.getReference(any(), anyLong())).thenReturn(product);
        when(likeRepository.save(any(Like.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(NotFoundException.class, () -> {
            likeService.toggleLike(PRODUCT_ID, USER_ID);
        });

        verify(likeRepository).existsByProduct_IdAndUserId(PRODUCT_ID, USER_ID);
        verify(entityManager).getReference(Product.class, PRODUCT_ID);
        verifyNoMoreInteractions(likeRepository);
    }
}

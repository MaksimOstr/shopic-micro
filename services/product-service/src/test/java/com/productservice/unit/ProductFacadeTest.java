package com.productservice.unit;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.LikeService;
import com.productservice.services.ProductFacade;
import com.productservice.services.ProductService;
import com.productservice.services.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductFacadeTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductService productService;

    @Mock
    private S3Service s3Service;

    @Mock
    private LikeService likeService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductFacade productFacade;

    private UUID productId;
    private UUID userId;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        userId = UUID.randomUUID();

        product = Product.builder()
                .id(productId)
                .name("Product")
                .imageUrl("old.png")
                .build();
    }

    @Test
    void createProduct_shouldUploadImageAndCreateProduct() {
        CreateProductRequest request = new CreateProductRequest(
                "Product",
                "Desc",
                BigDecimal.TEN,
                UUID.randomUUID(),
                UUID.randomUUID(),
                10
        );

        when(s3Service.uploadFile(any(), eq(multipartFile)))
                .thenReturn("image.png");
        when(productService.create(request, "image.png"))
                .thenReturn(product);

        AdminProductDto dto = new AdminProductDto(
                productId,
                "Product",
                "Desc",
                "image.png",
                10,
                BigDecimal.TEN,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Category",
                "Brand",
                false,
                Instant.now()
        );

        when(productMapper.toAdminProductDto(product)).thenReturn(dto);

        AdminProductDto result =
                productFacade.createProduct(request, multipartFile);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void updateProduct_shouldUpdateAndReturnDto() {
        UpdateProductRequest request = new UpdateProductRequest(
                "New",
                "Desc",
                false,
                BigDecimal.ONE,
                null,
                null,
                5
        );

        when(productService.updateProduct(productId, request))
                .thenReturn(product);

        AdminProductDto dto = mock(AdminProductDto.class);
        when(productMapper.toAdminProductDto(product)).thenReturn(dto);

        AdminProductDto result =
                productFacade.updateProduct(productId, request);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void changeProductImage_shouldReplaceAndDeleteOldImage() {
        when(productService.existsById(productId)).thenReturn(true);
        when(s3Service.uploadFile(any(), eq(multipartFile)))
                .thenReturn("new.png");
        when(productService.updateProductImage(productId, "new.png"))
                .thenReturn("old.png");

        productFacade.changeProductImage(productId, multipartFile);

        verify(s3Service).delete("old.png");
    }

    @Test
    void changeProductImage_shouldThrow_whenProductNotFound() {
        when(productService.existsById(productId)).thenReturn(false);

        assertThatThrownBy(() ->
                productFacade.changeProductImage(productId, multipartFile))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product not found");
    }

    @Test
    void getUserProduct_shouldReturnLikedProduct_whenUserProvided() {
        when(productService.getActiveWithCategoryAndBrandById(productId))
                .thenReturn(product);

        UserProductDto dto = new UserProductDto(
                productId, "Product", "Desc", "img",
                10, BigDecimal.TEN, "Brand", "Category"
        );

        when(productMapper.toUserProductDto(product)).thenReturn(dto);
        when(likeService.existsByProductIdAndUserId(productId, userId))
                .thenReturn(true);

        UserProductDto result =
                productFacade.getUserProduct(productId, userId);

        assertThat(result.isLiked()).isTrue();
    }

    @Test
    void getUserProduct_shouldNotEnrich_whenUserIsNull() {
        when(productService.getActiveWithCategoryAndBrandById(productId))
                .thenReturn(product);

        UserProductDto dto = new UserProductDto(
                productId, "Product", "Desc", "img",
                10, BigDecimal.TEN, "Brand", "Category"
        );

        when(productMapper.toUserProductDto(product)).thenReturn(dto);

        UserProductDto result =
                productFacade.getUserProduct(productId, null);

        assertThat(result.isLiked()).isFalse();
        verify(likeService, never()).existsByProductIdAndUserId(any(), any());
    }

    @Test
    void getAdminProduct_shouldReturnAdminDto() {
        when(productService.getProductById(productId))
                .thenReturn(product);

        AdminProductDto dto = mock(AdminProductDto.class);
        when(productMapper.toAdminProductDto(product)).thenReturn(dto);

        AdminProductDto result =
                productFacade.getAdminProduct(productId);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getLikedProducts_shouldReturnMappedList() {
        Set<UUID> likedIds = Set.of(productId);

        when(likeService.getLikedProductIds(userId))
                .thenReturn(likedIds);
        when(productService.getActiveProductsByIds(likedIds))
                .thenReturn(List.of(product));

        LikedProductDto dto = mock(LikedProductDto.class);
        when(productMapper.toLikedProductDtoList(List.of(product)))
                .thenReturn(List.of(dto));

        List<LikedProductDto> result =
                productFacade.getLikedProducts(userId);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void toggleLike_shouldDelegateToLikeService() {
        when(productService.getProductById(productId))
                .thenReturn(product);

        productFacade.toggleLike(productId, userId);

        verify(likeService).toggleLike(product, userId);
    }
}

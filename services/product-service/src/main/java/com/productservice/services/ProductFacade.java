package com.productservice.services;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.AdminProductPreviewDto;
import com.productservice.dto.UserProductPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.ProductMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.productservice.utils.ProductUtils.buildAdminProductSpec;
import static com.productservice.utils.ProductUtils.buildUserProductSpec;


@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final S3Service s3Service;
    private final LikeService likeService;

    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";

    @Transactional
    public AdminProductDto createProduct(CreateProductRequest dto, MultipartFile productImage) {
        String imageUrl = s3Service.uploadFile(PRODUCT_IMAGE_BUCKET, productImage);
        Product product = productService.create(dto, imageUrl);

        return productMapper.toAdminProductDto(product);
    }

    public AdminProductDto updateProduct(UUID id, UpdateProductRequest dto) {
        Product product = productService.updateProduct(id, dto);

        return productMapper.toAdminProductDto(product);
    }

    public void changeProductImage(UUID id, MultipartFile productImage) {
        if(!productService.existsById(id)) {
            throw new NotFoundException("Product not found");
        }
        String imageUrl = s3Service.uploadFile(PRODUCT_IMAGE_BUCKET, productImage);
        String oldImageUrl = productService.updateProductImage(id,  imageUrl);

        if(oldImageUrl != null) {
            s3Service.delete(oldImageUrl);
        }
    }

    public UserProductDto getUserProduct(UUID productId, UUID userId) {
        Product product = productService.getActiveProductById(productId);
        UserProductDto productDto = productMapper.toUserProductDto(product);

        if(userId != null) {
            enrichProduct(productDto, userId);
        }

        return productDto;
    }

    @Transactional
    public AdminProductDto getAdminProduct(UUID productId) {
        Product product = productService.getProductById(productId);
        return productMapper.toAdminProductDto(product);
    }

    public List<LikedProductDto> getLikedProducts(UUID userId) {
        Set<UUID> productIds = likeService.getLikedProductIds(userId);

        List<Product> likedProducts = productService.getProductsByIds(productIds);

        return productMapper.toLikedProductDtoList(likedProducts);
    }

    public void toggleLike(UUID productId, UUID userId) {
        likeService.toggleLike(productId, userId);
    }

    public Page<UserProductPreviewDto> getProductsByFilters(UserProductParams params, Pageable pageable, UUID userId) {
        Specification<Product> spec = buildUserProductSpec(params);
        Page<Product> productPage = productService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<UserProductPreviewDto> previewDtoList = productMapper.toProductUserPreviewDtoList(productList);

        if(userId != null) {
            enrichProductList(previewDtoList, userId);
        }

        return  new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }

    public Page<AdminProductPreviewDto> getProductsByFilters(AdminProductParams params, Pageable pageable) {
        Specification<Product> spec = buildAdminProductSpec(params);
        Page<Product> productPage = productService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<AdminProductPreviewDto> previewDtoList = productMapper.toProductAdminPreviewDtoList(productList);

        return new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }


    private void enrichProduct(UserProductDto product, UUID userId) {
        Boolean isLiked = likeService.isProductLiked(product.getId(), userId);

        product.setIsLiked(isLiked);
    }

    private void enrichProductList(List<UserProductPreviewDto> products, UUID userId) {
        Set<UUID> likesIds = likeService.getLikedProductIds(userId);

        for (UserProductPreviewDto product : products) {
            product.setIsLiked(likesIds.contains(product.getId()));
        }
    }

}

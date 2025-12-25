package com.productservice.services;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.productservice.utils.ProductUtils.buildAdminProductSpec;
import static com.productservice.utils.ProductUtils.buildUserProductSpec;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final S3Service s3Service;
    private final LikeEnrichmentService likeEnrichmentService;
    private final LikeService likeService;

    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";

    @Transactional
    public AdminProductDto createProduct(CreateProductRequest dto, MultipartFile productImage) {
        Brand brand = brandService.getBrandById(dto.brandId());
        Category category = categoryService.getCategoryById(dto.categoryId());
        String imageUrl = s3Service.uploadFile(PRODUCT_IMAGE_BUCKET, productImage);
        Product product = productService.create(dto, imageUrl, category, brand);

        return productMapper.toAdminProductDto(product);
    }

    @Transactional
    public AdminProductDto updateProduct(UUID id, UpdateProductRequest dto) {
        Product product = productService.getProductById(id);

        updateProductFields(product, dto);

        return productMapper.toAdminProductDto(product);
    }

    @Transactional
    public void changeProductImage(UUID id, MultipartFile productImage) {
        Product product = productService.getProductById(id);
        Optional<String> optionalProductImageUrl = productService.getOptionalProductImageUrl(id);

        optionalProductImageUrl.ifPresent(s3Service::delete);

        String imageUrl = s3Service.uploadFile(PRODUCT_IMAGE_BUCKET, productImage);

        product.setImageUrl(imageUrl);
    }

    public UserProductDto getUserProduct(UUID productId, UUID userId) {
        Product product = productService.getActiveProductById(productId);
        UserProductDto productDto = productMapper.toUserProductDto(product);

        likeEnrichmentService.enrichProduct(productDto, userId);

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

    public Page<ProductUserPreviewDto> getProductsByFilters(UserProductParams params, Pageable pageable, UUID userId) {
        Specification<Product> spec = buildUserProductSpec(params);
        Page<Product> productPage = productService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductUserPreviewDto> previewDtoList = productMapper.toProductUserPreviewDtoList(productList);

        likeEnrichmentService.enrichProductList(previewDtoList, userId);

        return  new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }

    public Page<ProductAdminPreviewDto> getProductsByFilters(AdminProductParams params, Pageable pageable) {
        Specification<Product> spec = buildAdminProductSpec(params);
        Page<Product> productPage = productService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductAdminPreviewDto> previewDtoList = productMapper.toProductAdminPreviewDtoList(productList);

        return new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }


    private void updateProductFields(Product product, UpdateProductRequest dto) {
        Optional.ofNullable(dto.name()).ifPresent(product::setName);
        Optional.ofNullable(dto.description()).ifPresent(product::setDescription);
        Optional.ofNullable(dto.price()).ifPresent(product::setPrice);
        Optional.ofNullable(dto.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(dto.categoryId()).ifPresent(categoryId -> {
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);
        });
        Optional.ofNullable(dto.brandId()).ifPresent(brandId -> {
            Brand brand = brandService.getBrandById(brandId);
            product.setBrand(brand);
        });
    }
}

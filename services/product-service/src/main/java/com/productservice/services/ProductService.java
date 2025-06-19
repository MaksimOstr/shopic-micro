package com.productservice.services;

import com.productservice.dto.request.*;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.ProductMapper;
import com.productservice.projection.ProductDto;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductPrice;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.productservice.utils.ProductUtils.PRODUCT_NOT_FOUND;
import static com.productservice.utils.ProductUtils.buildSpecification;
import static com.productservice.utils.Utils.getUUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final LikeService likeService;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;


    public CompletableFuture<Product> create(CreateProductRequest dto, MultipartFile productImage) {
        Category category = categoryService.findById(dto.categoryId());
        Brand brand = brandService.getBrandById(dto.brandId());

        return productImageService.uploadProductImage(productImage).thenApply(url -> {
            Product product =  createProductEntity(dto, url, category, brand);

            return productRepository.save(product);
        });
    }

    @Transactional
    public Product updateProduct(UpdateProductRequest dto, long productId) {
        Product product = getProductById(productId);

        updateProductFieldsIfExists(product, dto);

        return product;
    }

    public CompletableFuture<Void> updateProductImage(long productId, MultipartFile productImage) {
        String imageUrl = getProductImageUrl(productId);

        return productImageService.uploadProductImage(productImage)
                .thenAccept(newImageUrl -> {
                    int updated = productRepository.updateProductImageUrl(productId, newImageUrl);
                    if (updated == 0) {
                        log.error("Failed to update product image url");
                        throw new NotFoundException(PRODUCT_NOT_FOUND);
                    }

                    productImageService.deleteImage(imageUrl);
                });
    }

    public List<Product> getProductsForUpdate(List<Long> productIds) {
        return productRepository.findProductsForUpdate(productIds);
    }

    public void deleteProductById(long productId) {
        String imageUrl = getProductImageUrl(productId);

        productImageService.deleteImage(imageUrl);
        productRepository.deleteProductById(productId);
    }

    public Page<ProductDto> findPublicProductsByFilters(ProductParams dto, Pageable pageable, long userId) {
        return getPageOfProductsByFilters(dto, pageable, true, userId);
    }

    public Page<ProductDto> findAdminProductsByFilters(AdminProductParams dto, Pageable pageable, long userId) {
        return getPageOfProductsByFilters(dto, pageable, dto.getEnabled(), userId);
    }

    public List<ProductDto> getLikedProducts(long userId) {
        Set<Long> likedProducts = likeService.getLikedProductIds(userId);

        return productRepository.findProductsByIds(likedProducts);
    }

    public List<ProductPrice> getProductPrices(List<Long> productIds) {
        return productRepository.findProductPrices(productIds);
    }

    public Product getProductBySku(UUID sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Product getEnabledProductById(long id) {
        return productRepository.getEnabledProducts(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public ProductForCartDto getProductInfoForCart(long productId) {
        return productRepository.getProductForCartById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public Page<ProductDto> getPageOfProducts(Pageable pageable, long userId) {
        List<ProductDto> products = productRepository.getPageOfProducts(pageable).getContent();

        markLikedProducts(products, userId);

        return new PageImpl<>(products, pageable, pageable.getPageSize());
    }



    private String getProductImageUrl(long productId) {
        return productRepository.getProductImageUrl(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    private Page<ProductDto> getPageOfProductsByFilters(ProductParams params, Pageable pageable, Boolean enabled, long userId) {
        Specification<Product> spec = buildSpecification(params, enabled);

        List<Product> products = productRepository.findAll(spec, pageable).getContent();
        List<ProductDto> productDtoList = products.stream().map(productMapper::productToProductDto).toList();

        markLikedProducts(productDtoList, userId);

        return new PageImpl<>(productDtoList, pageable, productDtoList.size());
    }

    private void markLikedProducts(List<ProductDto> products, long userId) {
        Set<Long> likesIds = likeService.getLikedProductIds(userId);

        for (ProductDto product : products) {
            product.setLiked(likesIds.contains(product.getId()));
        }
    }

    private void updateProductFieldsIfExists(Product product, UpdateProductRequest dto) {
        Optional.ofNullable(dto.name()).ifPresent(product::setName);
        Optional.ofNullable(dto.description()).ifPresent(product::setDescription);
        Optional.ofNullable(dto.price()).ifPresent(product::setPrice);
        Optional.ofNullable(dto.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(dto.enabled()).ifPresent(product::setEnabled);
        Optional.ofNullable(dto.categoryId()).ifPresent(categoryId -> {
            Category category = categoryService.findById(categoryId);
            product.setCategory(category);
        });
        Optional.ofNullable(dto.brandId()).ifPresent(brandId -> {
            Brand brand = brandService.getBrandById(brandId);
            product.setBrand(brand);
        });
    }

    private Product createProductEntity(CreateProductRequest dto, String url, Category category, Brand brand) {
        return Product.builder()
                .name(dto.name())
                .description(dto.description())
                .sku(getUUID())
                .price(dto.price())
                .imageUrl(url)
                .enabled(dto.enabled())
                .category(category)
                .stockQuantity(dto.stockQuantity())
                .brand(brand)
                .build();
    }
}

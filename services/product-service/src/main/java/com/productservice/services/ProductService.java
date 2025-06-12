package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.GetProductsByFilters;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.ProductMapper;
import com.productservice.projection.ProductDto;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductImageUrlProjection;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.productservice.utils.SpecificationUtils.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final S3Service s3Service;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final LikeService likeService;
    private final ProductMapper productMapper;

    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";
    private static final String PRODUCT_NOT_FOUND = "Product Not Found";

    //OPTIMIZE QUERY
    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public CompletableFuture<Product> create(CreateProductRequest dto, MultipartFile productImage) {
        Category category = categoryService.findById(dto.categoryId());
        Brand brand = brandService.getBrandById(dto.brandId());

        return postProductPhoto(productImage).thenApply(url -> {
            Product product = Product.builder()
                    .name(dto.name())
                    .description(dto.description())
                    .sku(getSKU())
                    .price(dto.price())
                    .imageUrl(url)
                    .category(category)
                    .stockQuantity(dto.stockQuantity())
                    .brand(brand)
                    .build();


            return productRepository.save(product);
        });
    }


    @Transactional
    //OPTIMIZE QUERY
    public Product updateProduct(UpdateProductRequest dto, long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        Optional.ofNullable(dto.name()).ifPresent(product::setName);
        Optional.ofNullable(dto.description()).ifPresent(product::setDescription);
        Optional.ofNullable(dto.price()).ifPresent(product::setPrice);
        Optional.ofNullable(dto.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(dto.categoryId()).ifPresent(categoryId -> {
            Category category = categoryService.findById(categoryId);
            product.setCategory(category);
        });
        Optional.ofNullable(dto.brandId()).ifPresent(brandId -> {
            Brand brand = brandService.getBrandById(brandId);
            product.setBrand(brand);
        });

        return product;
    }

    public Page<ProductDto> findProductsByFilters(GetProductsByFilters dto, Pageable pageable, long userId) {
        Specification<Product> spec = iLike("name", dto.name())
                .and(lte("price", dto.toPrice()))
                .and(gte("price", dto.fromPrice()))
                .and(hasChild("category", dto.categoryId()))
                .and(hasChild("brand", dto.brandId()));

        List<Product> products = productRepository.findAll(spec, pageable).getContent();
        List<ProductDto> productDto = products.stream().map(productMapper::productToProductDto).toList();

        markLikedProducts(productDto, userId);

        return new PageImpl<>(productDto, pageable, pageable.getPageSize());
    }

    public List<ProductDto> getLikedProducts(long userId) {
        Set<Long> likedProducts = likeService.getLikedProductIds(userId);

        return productRepository.findProductsByIds(likedProducts);
    }


    public Product getProductBySku(UUID sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }


    public void updateProductImage(long productId, MultipartFile productImage) {
        String imageUrl = getProductImageUrl(productId);
        s3Service.delete(imageUrl);

        postProductPhoto(productImage)
                .thenAccept(newImageUrl -> {
                    int updated = productRepository.updateProductImageUrl(productId, newImageUrl);
                    if (updated == 0) {
                        log.error("Failed to update product image url");
                        throw new NotFoundException(PRODUCT_NOT_FOUND);
                    }
                });
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


    public void deleteProductById(long productId) {
        Product product = getProductById(productId);

        s3Service.delete(product.getImageUrl());
        productRepository.delete(product);
    }


    private String getProductImageUrl(long productId) {
        return productRepository.getProductImageUrl(productId)
                .map(ProductImageUrlProjection::getImageUrl)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }


    private CompletableFuture<String> postProductPhoto(MultipartFile productImage) {
        return s3Service.uploadFile(new PutObjectDto(
                PRODUCT_IMAGE_BUCKET,
                getKey(),
                productImage
        ));
    }

    private String getKey() {
        return UUID.randomUUID().toString();
    }

    private UUID getSKU() {
        return UUID.randomUUID();
    }

    private void markLikedProducts(List<ProductDto> products, long userId) {
        Set<Long> likesIds = likeService.getLikedProductIds(userId);

        for (ProductDto product : products) {
            product.setLiked(likesIds.contains(product.getId()));
        }
    }
}

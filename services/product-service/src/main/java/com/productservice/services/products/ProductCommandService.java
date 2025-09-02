package com.productservice.services.products;

import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ProductRepository;
import com.productservice.services.BrandService;
import com.productservice.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.productservice.utils.ProductUtils.PRODUCT_NOT_FOUND;
import static com.productservice.utils.Utils.getUUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductQueryService productQueryService;
    private final ProductImageService productImageService;


    public CompletableFuture<Product> create(CreateProductRequest dto, MultipartFile productImage) {
        Category category = categoryService.findById(dto.categoryId());
        Brand brand = brandService.getBrandById(dto.brandId());

        return productImageService.uploadProductImage(productImage).thenApply(url -> {
            Product product = createProductEntity(dto, url, category, brand);

            return productRepository.save(product);
        });
    }

    @Transactional
    public Product updateProduct(UpdateProductRequest dto, long productId) {
        Product product = productQueryService.getProductWithCategoryAndBrand(productId);

        updateProductFields(product, dto);

        return product;
    }

    public CompletableFuture<Void> updateProductImage(long productId, MultipartFile productImage) {
        return productImageService.updateProductImage(productId,  productImage)
                .thenAccept(newImageUrl -> {
                    int updated = productRepository.updateProductImageUrl(productId, newImageUrl);

                    if (updated == 0) {
                        log.error("Failed to update product image url");
                        throw new NotFoundException(PRODUCT_NOT_FOUND);
                    }
                });
    }

    public void deleteProductById(long productId) {
        String imageUrl = productQueryService.getProductImageUrl(productId);

        productImageService.deleteImage(imageUrl);
        productRepository.deleteProductById(productId);
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

    private void updateProductFields(Product product, UpdateProductRequest dto) {
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
}

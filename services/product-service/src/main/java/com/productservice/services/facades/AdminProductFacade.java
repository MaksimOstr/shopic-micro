package com.productservice.services.facades;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.PutObjectDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.BrandService;
import com.productservice.services.CategoryService;
import com.productservice.services.RatingEnrichmentService;
import com.productservice.services.S3Service;
import com.productservice.services.ProductService;
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
import java.util.UUID;

import static com.productservice.utils.ProductUtils.buildAdminProductSpec;
import static com.productservice.utils.Utils.getUUID;


@Service
@RequiredArgsConstructor
public class AdminProductFacade {
    private final RatingEnrichmentService ratingEnrichmentService;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final S3Service s3Service;

    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";

    @Transactional
    public AdminProductDto createProduct(CreateProductRequest dto, MultipartFile productImage) {
        Brand brand = brandService.getBrandById(dto.brandId());
        Category category = categoryService.getCategoryById(dto.categoryId());
        String imageUrl = s3Service.uploadFile(createPutObjectDto(productImage));
        Product product = productService.create(dto, imageUrl, category, brand);

        return productMapper.productToAdminProductDto(product);
    }

    @Transactional
    public AdminProductDto updateProduct(long id, UpdateProductRequest dto) {
        Product product = productService.getProductById(id);

        updateProductFields(product, dto);

        return productMapper.productToAdminProductDto(product);
    }

    @Transactional
    public void changeProductImage(long id, MultipartFile productImage) {
        Product product = productService.getProductById(id);
        Optional<String> optionalProductImageUrl = productService.getOptionalProductImageUrl(id);

        optionalProductImageUrl.ifPresent(s3Service::delete);

        String imageUrl = s3Service.uploadFile(createPutObjectDto(productImage));

        product.setImageUrl(imageUrl);
    }

    @Transactional
    public AdminProductDto getAdminProduct(long productId) {
        AdminProductDto product = productService.getAdminProductById(productId);

        ratingEnrichmentService.enrichProduct(product);

        return product;
    }

    @Transactional
    public AdminProductDto getAdminProduct(UUID sku) {
        AdminProductDto product = productService.getAdminProductBySku(sku);

        ratingEnrichmentService.enrichProduct(product);

        return product;
    }

    public Page<ProductAdminPreviewDto> getProductsByFilters(AdminProductParams params, Pageable pageable) {
        Specification<Product> spec = buildAdminProductSpec(params);
        Page<Product> productPage = productService.getProductPageBySpec(spec, pageable);
        List<Product> productList = productPage.getContent();
        List<ProductAdminPreviewDto> previewDtoList = productMapper.productToProductAdminPreviewDtoList(productList);

        ratingEnrichmentService.enrichProductList(previewDtoList);

        return new PageImpl<>(previewDtoList, pageable, productPage.getTotalElements());
    }

    public void activateProduct(long productId) {
        productService.activateProduct(productId);
    }

    public void archiveProduct(long productId) {
        productService.archiveProduct(productId);
    }

    private PutObjectDto createPutObjectDto(MultipartFile productImage) {
        return new PutObjectDto(
                PRODUCT_IMAGE_BUCKET,
                getUUID().toString(),
                productImage
        );
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

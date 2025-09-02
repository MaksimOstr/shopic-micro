package com.productservice.services.products;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.services.grpc.GrpcReviewService;
import com.shopic.grpc.reviewservice.ProductRatingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.productservice.utils.SpecificationUtils.*;
import static com.productservice.utils.SpecificationUtils.gte;
import static com.productservice.utils.SpecificationUtils.hasChild;

@Service
@RequiredArgsConstructor
public class AdminProductFacade {
    private final ProductQueryService productQueryService;
    private final ProductSearchService productSearchService;
    private final ProductCommandService productCommandService;
    private final GrpcReviewService grpcReviewService;


    public CompletableFuture<Product> createProduct(CreateProductRequest dto, MultipartFile productImage) {
        return productCommandService.create(dto, productImage);
    }

    public void deleteProduct(long productId) {
        productCommandService.deleteProductById(productId);
    }

    public AdminProductDto getAdminProduct(long id) {
        AdminProductDto product = productQueryService.getAdminProductById(id);
        ProductRatingResponse rating = grpcReviewService.getProductRating(product.getId());

        product.setRating(new BigDecimal(rating.getRating()));

        return product;
    }

    public Product updateProduct(UpdateProductRequest dto, long productId) {
        return productCommandService.updateProduct(dto, productId);
    }

    public Page<ProductAdminPreviewDto> getProductsByFilters(AdminProductParams params, Pageable pageable, long userId) {
        Specification<Product> spec = iLike("name", params.getName())
                .and(hasActiveStatus("enabled", params.getEnabled()))
                .and(lte("price", params.getToPrice()))
                .and(gte("price", params.getFromPrice()))
                .and(hasChild("category", params.getCategoryId()))
                .and(hasChild("brand", params.getBrandId()));

        return productSearchService.getPageOfAdminProductsByFilters(spec, pageable, userId);
    }

    public CompletableFuture<Void> updateProductImage(long productId, MultipartFile productImage) {
        return productCommandService.updateProductImage(productId, productImage);
    }

    public Product getProductBySku(UUID sku) {
        return productQueryService.getProductBySku(sku);
    }
}

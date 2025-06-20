package com.productservice.services.products;

import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.ProductParams;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.projection.ProductDto;
import com.productservice.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.productservice.utils.ProductUtils.buildSpecification;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductQueryService productQueryService;
    private final ProductMapper productMapper;
    private final LikeService likeService;


    public List<ProductDto> getLikedProducts(long userId) {
        Set<Long> likedProducts = likeService.getLikedProductIds(userId);

        return productQueryService.getProductsByIds(likedProducts);
    }

    public Page<ProductDto> getPageOfProducts(Pageable pageable, long userId) {
        Page<ProductDto>  productPage = productQueryService.getProductPage(pageable);
        List<ProductDto> products = productPage.getContent();

        markLikedProducts(products, userId);

        return new PageImpl<>(products, pageable, productPage.getTotalElements());
    }

    public Page<ProductDto> findPublicProductsByFilters(ProductParams dto, Pageable pageable, long userId) {
        return getPageOfProductsByFilters(dto, pageable, true, userId);
    }

    public Page<ProductDto> findAdminProductsByFilters(AdminProductParams dto, Pageable pageable, long userId) {
        return getPageOfProductsByFilters(dto, pageable, dto.getEnabled(), userId);
    }


    private void markLikedProducts(List<ProductDto> products, long userId) {
        Set<Long> likesIds = likeService.getLikedProductIds(userId);

        for (ProductDto product : products) {
            product.setLiked(likesIds.contains(product.getId()));
        }
    }

    private Page<ProductDto> getPageOfProductsByFilters(ProductParams params, Pageable pageable, Boolean enabled, long userId) {
        Specification<Product> spec = buildSpecification(params, enabled);
        Page<Product> productPage = productQueryService.getProductPageBySpec(spec, pageable);
        List<Product> products = productPage.getContent();
        List<ProductDto> productDtoList = products.stream().map(productMapper::productToProductDto).toList();

        markLikedProducts(productDtoList, userId);

        return new PageImpl<>(productDtoList, pageable, productPage.getTotalElements());
    }
}

package com.productservice.unit;

import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ProductRepository;
import com.productservice.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private static final long PRODUCT_ID = 1L;

    private Product product;
    private Category category;
    private Brand brand;

    @BeforeEach
    public void setUp() {
        category = Category.builder().build();
        brand = Brand.builder().build();
        product = Product.builder()
                .id(PRODUCT_ID)
                .category(category)
                .brand(brand)
                .build();
    }

    @Test
    public void testActivateProduct_whenCalledWithNonExistingProduct_thenThrowException() {
        when(productRepository.getProductWithCategoryAndBrand(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            productService.activateProduct(PRODUCT_ID);
        });

        verify(productRepository).getProductWithCategoryAndBrand(PRODUCT_ID);
    }

    @Test
    public void testActivateProduct_whenCalledForAlreadyActivatedProduct_thenThrowException() {
        product.setStatus(ProductStatusEnum.ACTIVE);

        when(productRepository.getProductWithCategoryAndBrand(anyLong())).thenReturn(Optional.of(product));

        assertThrows(IllegalStateException.class, () -> {
            productService.activateProduct(PRODUCT_ID);
        });

        verify(productRepository).getProductWithCategoryAndBrand(PRODUCT_ID);
    }

    @Test
    public void testActivateProduct_whenCalledWithDeactivatedBrand_thenThrowException() {
        product.setStatus(ProductStatusEnum.ARCHIVED);
        brand.setActive(false);

        when(productRepository.getProductWithCategoryAndBrand(anyLong())).thenReturn(Optional.of(product));

        assertThrows(IllegalStateException.class, () -> {
            productService.activateProduct(PRODUCT_ID);
        });

        verify(productRepository).getProductWithCategoryAndBrand(PRODUCT_ID);

        assertEquals(ProductStatusEnum.ARCHIVED, product.getStatus());
    }

    @Test
    public void testActivateProduct_whenCalledWithDeactivatedCategory_thenThrowException() {
        product.setStatus(ProductStatusEnum.ARCHIVED);
        category.setActive(false);

        when(productRepository.getProductWithCategoryAndBrand(anyLong())).thenReturn(Optional.of(product));

        assertThrows(IllegalStateException.class, () -> {
            productService.activateProduct(PRODUCT_ID);
        });

        verify(productRepository).getProductWithCategoryAndBrand(PRODUCT_ID);

        assertEquals(ProductStatusEnum.ARCHIVED, product.getStatus());
    }

    @Test
    public void testActivateProduct_whenCalledWithArchivedProduct_thenActivateProduct() {
        product.setStatus(ProductStatusEnum.ARCHIVED);
        category.setActive(true);
        brand.setActive(true);

        when(productRepository.getProductWithCategoryAndBrand(anyLong())).thenReturn(Optional.of(product));

        productService.activateProduct(PRODUCT_ID);

        verify(productRepository).getProductWithCategoryAndBrand(PRODUCT_ID);

        assertEquals(ProductStatusEnum.ACTIVE, product.getStatus());
    }

    @Test
    public void testArchiveProduct_whenCalledWithNonExistingProduct_thenThrowException() {
        product.setStatus(ProductStatusEnum.ACTIVE);

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            productService.archiveProduct(PRODUCT_ID);
        });

        verify(productRepository).findById(PRODUCT_ID);

        assertEquals(ProductStatusEnum.ACTIVE, product.getStatus());
    }

    @Test
    public void testArchiveProduct_whenCalledWithArchivedProduct_thenThrowException() {
        product.setStatus(ProductStatusEnum.ARCHIVED);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertThrows(IllegalStateException.class, () -> {
            productService.archiveProduct(PRODUCT_ID);
        });

        verify(productRepository).findById(PRODUCT_ID);
    }

    @Test
    public void testArchiveProduct_whenCalledWithActiveProduct_thenArchiveProduct() {
        product.setStatus(ProductStatusEnum.ACTIVE);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        productService.archiveProduct(PRODUCT_ID);

        verify(productRepository).findById(PRODUCT_ID);

        assertEquals(ProductStatusEnum.ARCHIVED, product.getStatus());
    }
}

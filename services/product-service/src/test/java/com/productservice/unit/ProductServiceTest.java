package com.productservice.unit;

import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ProductRepository;
import com.productservice.services.BrandService;
import com.productservice.services.CategoryService;
import com.productservice.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static com.productservice.utils.ProductUtils.PRODUCT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BrandService brandService;

    private UUID productId;
    private UUID categoryId;
    private UUID brandId;

    private Category category;
    private Brand brand;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        brandId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);

        brand = new Brand();
        brand.setId(brandId);

        product = Product.builder()
                .id(productId)
                .name("Old name")
                .description("Old desc")
                .price(BigDecimal.TEN)
                .stockQuantity(10)
                .imageUrl("old.png")
                .category(category)
                .brand(brand)
                .isDeleted(false)
                .build();
    }


    @Test
    void create_whenBrandProvided_thenCreateProductWithBrand() {
        CreateProductRequest dto = new CreateProductRequest(
                "Product",
                "desc",
                BigDecimal.TEN,
                categoryId,
                brandId,
                5
        );

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(brandService.getBrandById(brandId)).thenReturn(brand);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.create(dto, "img.png");

        assertEquals("Product", result.getName());
        assertEquals("desc", result.getDescription());
        assertEquals(BigDecimal.TEN, result.getPrice());
        assertEquals(5, result.getStockQuantity());
        assertEquals("img.png", result.getImageUrl());
        assertEquals(category, result.getCategory());
        assertEquals(brand, result.getBrand());
        assertFalse(result.isDeleted());
    }

    @Test
    void create_whenBrandIsNull_thenCreateProductWithoutBrand() {
        CreateProductRequest dto = new CreateProductRequest(
                "Product",
                "desc",
                BigDecimal.TEN,
                categoryId,
                null,
                5
        );

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.create(dto, "img.png");

        assertNull(result.getBrand());
    }

    @Test
    void getActiveWithCategoryAndBrandById_whenExists_thenReturnProduct() {
        when(productRepository.findActiveWithCategoryAndBrandById(productId))
                .thenReturn(Optional.of(product));

        Product result =
                productService.getActiveWithCategoryAndBrandById(productId);

        assertSame(product, result);
    }

    @Test
    void getActiveWithCategoryAndBrandById_whenNotFound_thenThrowException() {
        when(productRepository.findActiveWithCategoryAndBrandById(productId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> productService.getActiveWithCategoryAndBrandById(productId)
        );

        assertEquals(PRODUCT_NOT_FOUND, ex.getMessage());
    }


    @Test
    void getActiveProductsByIds_whenCalled_thenReturnProducts() {
        when(productRepository.findAllActiveByIdList(List.of(productId)))
                .thenReturn(List.of(product));

        List<Product> result =
                productService.getActiveProductsByIds(List.of(productId));

        assertEquals(1, result.size());
    }

    @Test
    void getProductsByIdsWithLock_whenCalled_thenReturnProducts() {
        when(productRepository.findByIdInWithLock(List.of(productId)))
                .thenReturn(List.of(product));

        List<Product> result =
                productService.getProductsByIdsWithLock(List.of(productId));

        assertEquals(1, result.size());
    }


    @Test
    void updateProduct_whenAllFieldsProvided_thenUpdateAllFields() {
        UpdateProductRequest dto = new UpdateProductRequest(
                "New name",
                "New desc",
                true,
                BigDecimal.ONE,
                categoryId,
                brandId,
                20
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(categoryService.getCategoryById(categoryId))
                .thenReturn(category);
        when(brandService.getBrandById(brandId))
                .thenReturn(brand);
        when(productRepository.save(product))
                .thenReturn(product);

        Product result = productService.updateProduct(productId, dto);

        assertEquals("New name", result.getName());
        assertEquals("New desc", result.getDescription());
        assertEquals(BigDecimal.ONE, result.getPrice());
        assertEquals(20, result.getStockQuantity());
        assertTrue(result.isDeleted());
        assertEquals(category, result.getCategory());
        assertEquals(brand, result.getBrand());
    }

    @Test
    void updateProduct_whenOnlyNameProvided_thenUpdateOnlyName() {
        UpdateProductRequest dto = new UpdateProductRequest(
                "Only name",
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.save(product))
                .thenReturn(product);

        productService.updateProduct(productId, dto);

        assertEquals("Only name", product.getName());
        assertEquals("Old desc", product.getDescription());
    }

    @Test
    void updateProduct_whenProductNotFound_thenThrowException() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> productService.updateProduct(productId, mock(UpdateProductRequest.class))
        );
    }


    @Test
    void updateProductImage_whenCalled_thenReturnOldImageUrl() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        String oldUrl =
                productService.updateProductImage(productId, "new.png");

        assertEquals("old.png", oldUrl);
        assertEquals("new.png", product.getImageUrl());
    }


    @Test
    void existsById_whenProductExists_thenReturnTrue() {
        when(productRepository.existsById(productId))
                .thenReturn(true);

        assertTrue(productService.existsById(productId));
    }

    @Test
    void existsById_whenProductDoesNotExist_thenReturnFalse() {
        when(productRepository.existsById(productId))
                .thenReturn(false);

        assertFalse(productService.existsById(productId));
    }


    @Test
    void getProductPageBySpec_whenCalled_thenReturnPage() {
        Pageable pageable = Pageable.ofSize(10);
        Specification<Product> spec = (root, q, cb) -> null;

        Page<Product> page =
                new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll(spec, pageable))
                .thenReturn(page);

        Page<Product> result =
                productService.getProductPageBySpec(spec, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(product, result.getContent().get(0));
    }

    @Test
    void getProductById_whenExists_thenReturnProduct() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        Product result = productService.getProductById(productId);

        assertSame(product, result);
    }

    @Test
    void getProductById_whenNotFound_thenThrowException() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> productService.getProductById(productId)
        );

        assertEquals(PRODUCT_NOT_FOUND, ex.getMessage());
    }
}

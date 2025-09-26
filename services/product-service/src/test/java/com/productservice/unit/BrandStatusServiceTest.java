package com.productservice.unit;

import com.productservice.dto.response.BrandDeactivationCheckResponse;
import com.productservice.dto.response.BrandDeactivationResponse;
import com.productservice.entity.Brand;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.services.BrandService;
import com.productservice.services.BrandStatusService;
import com.productservice.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.NotActiveException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandStatusServiceTest {
    @Mock
    private BrandService brandService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BrandStatusService brandStatusService;


    private static final int BRAND_ID = 1;
    private static final String BRAND_NAME = "testBrandName";
    private static final int ACTIVE_COUNT = 5;
    private static final int DEACTIVATED_COUNT = 10;

    private Brand brand;

    @BeforeEach
    public void setup() {
        brand = Brand.builder()
                .id(BRAND_ID)
                .name(BRAND_NAME)
                .build();
    }

    @Test
    public void testDeactivationCheck_whenCalledWithNotExistingBrand_thenThrowException() {
        when(brandService.getBrandById(anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            brandStatusService.deactivationCheck(BRAND_ID);
        });

        verify(brandService).getBrandById(BRAND_ID);
        verifyNoInteractions(productService);
    }

    @Test
    public void testDeactivationCheck_whenCalledWithExistingBrand_thenReturnResult() {
        when(brandService.getBrandById(anyInt())).thenReturn(brand);
        when(productService.countProductsByBrandIdAndStatus(anyInt(), any(ProductStatusEnum.class))).thenReturn(ACTIVE_COUNT);

        BrandDeactivationCheckResponse result = brandStatusService.deactivationCheck(BRAND_ID);

        verify(brandService).getBrandById(BRAND_ID);
        verify(productService).countProductsByBrandIdAndStatus(BRAND_ID, ProductStatusEnum.ACTIVE);

        assertEquals(BRAND_ID, result.brandId());
        assertEquals(BRAND_NAME, result.brandName());
        assertEquals(ACTIVE_COUNT, result.activeProductsCount());
    }

    @Test
    public void testDeactivate_whenCalledWithNotExistingBrand_thenThrowException() {
        when(brandService.getBrandById(anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            brandStatusService.deactivate(BRAND_ID);
        });

        verify(brandService).getBrandById(BRAND_ID);
        verifyNoInteractions(productService);
    }

    @Test
    public void testDeactivate_whenCalledWithDeactivatedBrand_thenThrowException() {
        brand.setActive(false);

        when(brandService.getBrandById(anyInt())).thenReturn(brand);

        assertThrows(IllegalStateException.class, () -> {
            brandStatusService.deactivate(BRAND_ID);
        });

        verify(brandService).getBrandById(BRAND_ID);
        verifyNoInteractions(productService);
    }

    @Test
    public void testDeactivate_whenCalledWithActiveBrand_thenDeactivate() {
        brand.setActive(true);

        when(brandService.getBrandById(anyInt())).thenReturn(brand);
        when(productService.deactivateByBrandId(anyInt())).thenReturn(DEACTIVATED_COUNT);

        BrandDeactivationResponse result = brandStatusService.deactivate(BRAND_ID);

        verify(brandService).getBrandById(BRAND_ID);
        verify(productService).deactivateByBrandId(BRAND_ID);

        assertFalse(brand.isActive());
        assertEquals(BRAND_ID, result.brandId());
        assertEquals(BRAND_NAME, result.brandName());
        assertEquals(DEACTIVATED_COUNT, result.deactivatedProductCount());
    }
}

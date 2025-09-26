package com.productservice.unit;

import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.exceptions.AlreadyExistsException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.BrandRepository;
import com.productservice.services.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;


    private static final String BRAND_NAME = "brandNameTest";
    private static final String REQUESTED_BRAND_NAME = "requestedBrandName";
    private static final int BRAND_ID = 1;
    private static final boolean ACTIVE = true;
    private static final UpdateBrandRequest UPDATE_BRAND_REQUEST = new UpdateBrandRequest(
            REQUESTED_BRAND_NAME
    );
    private static final CreateBrandRequest CREATE_BRAND_REQUEST = new CreateBrandRequest(
            REQUESTED_BRAND_NAME,
            ACTIVE
    );


    private Brand brand;

    @BeforeEach
    public void setup() {
        brand = Brand.builder()
                .id(BRAND_ID)
                .name(BRAND_NAME)
                .build();
    }

    @Test
    public void testUpdateBrand_whenCalledWithExistingBrand_thenThrowException() {
        when(brandRepository.existsBrandByName(anyString())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> {
            brandService.updateBrand(BRAND_ID, UPDATE_BRAND_REQUEST);
        });


        verify(brandRepository).existsBrandByName(REQUESTED_BRAND_NAME);
        verifyNoMoreInteractions(brandRepository);

        assertEquals(BRAND_NAME, brand.getName());
    }

    @Test
    public void testUpdateBrand_whenCalledWithNewBrandName_thenUpdateBrand() {
        when(brandRepository.existsBrandByName(anyString())).thenReturn(false);
        when(brandRepository.findById(anyInt())).thenReturn(Optional.of(brand));

        Brand result = brandService.updateBrand(BRAND_ID, UPDATE_BRAND_REQUEST);

        verify(brandRepository).existsBrandByName(REQUESTED_BRAND_NAME);
        verify(brandRepository).findById(BRAND_ID);

        assertEquals(REQUESTED_BRAND_NAME, brand.getName());
        assertEquals(brand, result);
    }

    @Test
    public void testUpdateBrand_whenCalledWithNonExistingBrandEntity_thenThrowException() {
        when(brandRepository.existsBrandByName(anyString())).thenReturn(false);
        when(brandRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            brandService.updateBrand(BRAND_ID, UPDATE_BRAND_REQUEST);
        });

        verify(brandRepository).existsBrandByName(REQUESTED_BRAND_NAME);
        verify(brandRepository).findById(BRAND_ID);

        assertEquals(BRAND_NAME, brand.getName());
    }

    @Test
    public void testCreate_whenCalledWithExistingBrandName_thenThrowException() {
        when(brandRepository.existsBrandByName(anyString())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> {
            brandService.create(CREATE_BRAND_REQUEST);
        });

        verify(brandRepository).existsBrandByName(REQUESTED_BRAND_NAME);
        verifyNoMoreInteractions(brandRepository);
    }

    @Test
    public void testCreate_whenCalledWithNewBrandName_thenCreateNewBrand() {
        ArgumentCaptor<Brand> brandCaptor = ArgumentCaptor.forClass(Brand.class);

        when(brandRepository.existsBrandByName(anyString())).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        Brand result = brandService.create(CREATE_BRAND_REQUEST);

        verify(brandRepository).existsBrandByName(REQUESTED_BRAND_NAME);
        verify(brandRepository).save(brandCaptor.capture());

        Brand capturedBrand = brandCaptor.getValue();

        assertEquals(REQUESTED_BRAND_NAME, capturedBrand.getName());
        assertEquals(ACTIVE, capturedBrand.isActive());
        assertEquals(brand, result);
    }


}

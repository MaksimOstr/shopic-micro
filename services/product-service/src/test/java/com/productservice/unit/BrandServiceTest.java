package com.productservice.unit;

import com.productservice.dto.AdminBrandDto;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.exceptions.ApiException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.BrandMapper;
import com.productservice.repository.BrandRepository;
import com.productservice.services.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private BrandService brandService;

    private UUID brandId;
    private Brand brand;

    @BeforeEach
    void setUp() {
        brandId = UUID.randomUUID();
        brand = Brand.builder()
                .id(brandId)
                .name("Nike")
                .isActive(true)
                .build();
    }

    @Test
    void create_shouldSaveBrand_whenNameDoesNotExist() {
        CreateBrandRequest request = new CreateBrandRequest("Adidas", true);
        AdminBrandDto expectedDto = new AdminBrandDto(UUID.randomUUID(), "Adidas", true);

        when(brandRepository.existsBrandByName(request.brandName())).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenAnswer(i -> i.getArgument(0));
        when(brandMapper.toAdminBrandDto(any(Brand.class))).thenReturn(expectedDto);

        AdminBrandDto result = brandService.create(request);

        assertThat(result).isEqualTo(expectedDto);

        ArgumentCaptor<Brand> brandCaptor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepository).save(brandCaptor.capture());
        Brand savedBrand = brandCaptor.getValue();

        assertThat(savedBrand.getName()).isEqualTo("Adidas");
        assertThat(savedBrand.isActive()).isTrue();
    }

    @Test
    void create_shouldThrowException_whenNameExists() {
        CreateBrandRequest request =
                new CreateBrandRequest("Nike", true);

        when(brandRepository.existsBrandByName(request.brandName()))
                .thenReturn(true);

        assertThatThrownBy(() -> brandService.create(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Brand name already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(brandRepository, never()).save(any());
    }

    @Test
    void updateBrand_shouldUpdateBrand_whenNameIsUnique() {
        UpdateBrandRequest request =
                new UpdateBrandRequest("Puma", false);

        when(brandRepository.existsByNameAndIdNot(request.brandName(), brandId))
                .thenReturn(false);
        when(brandRepository.findById(brandId))
                .thenReturn(Optional.of(brand));

        AdminBrandDto dto =
                new AdminBrandDto(brandId, "Puma", false);

        when(brandMapper.toAdminBrandDto(brand))
                .thenReturn(dto);

        AdminBrandDto result = brandService.updateBrand(brandId, request);

        assertThat(result).isEqualTo(dto);
        assertThat(brand.getName()).isEqualTo("Puma");
        assertThat(brand.isActive()).isFalse();
    }

    @Test
    void updateBrand_shouldThrowException_whenNameExists() {
        UpdateBrandRequest request =
                new UpdateBrandRequest("Nike", true);

        when(brandRepository.existsByNameAndIdNot(request.brandName(), brandId))
                .thenReturn(true);

        assertThatThrownBy(() -> brandService.updateBrand(brandId, request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Brand with name Nike already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getBrandById_shouldReturnBrand_whenExists() {
        when(brandRepository.findById(brandId))
                .thenReturn(Optional.of(brand));

        Brand result = brandService.getBrandById(brandId);

        assertThat(result).isEqualTo(brand);
    }

    @Test
    void getBrandById_shouldThrowException_whenNotFound() {
        when(brandRepository.findById(brandId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.getBrandById(brandId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Brand not found");
    }
}

package com.productservice.services;

import com.productservice.dto.AdminBrandDto;
import com.productservice.dto.UserBrandDto;
import com.productservice.dto.request.AdminBrandParams;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.exceptions.ApiException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.BrandMapper;
import com.productservice.repository.BrandRepository;
import com.productservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.productservice.utils.SpecificationUtils.equalsField;


@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;


    public Brand create(CreateBrandRequest dto) {
        if(existsByName(dto.brandName())) {
            throw new ApiException("Brand name already exists", HttpStatus.CONFLICT);
        }

        Brand brand = Brand.builder()
                .name(dto.brandName())
                .isActive(dto.isActive())
                .build();

        return brandRepository.save(brand);
    }

    @Transactional
    public AdminBrandDto updateBrand(UUID id, UpdateBrandRequest dto) {
        if(existsByName(dto.brandName())) {
            throw new ApiException("Brand with name " + dto.brandName() + " already exists", HttpStatus.CONFLICT);
        }

        Brand brand = getBrandById(id);

        brand.setName(dto.brandName());
        brand.setActive(dto.isActive());

        return brandMapper.toAdminBrandDto(brand);
    }

    public Page<UserBrandDto> searchUserBrands(String name, Pageable pageable) {
        Specification<Brand> spec = SpecificationUtils.<Brand>iLike("name", name)
                .and(equalsField("isActive", true));

        Page<Brand> brandPage = brandRepository.findAll(spec, pageable);
        List<Brand> brandList = brandPage.getContent();
        List<UserBrandDto> userBrandDtoList = brandMapper.toUserBrandDtoList(brandList);

        return new PageImpl<>(userBrandDtoList, pageable, brandPage.getTotalElements());
    }

    public Page<AdminBrandDto> searchAdminBrands(AdminBrandParams params, Pageable pageable) {
        Specification<Brand> spec = SpecificationUtils.<Brand>iLike("name", params.name())
                .and(equalsField("isActive", params.isActive()));
        Page<Brand> brandPage = brandRepository.findAll(spec, pageable);
        List<Brand> brandList = brandPage.getContent();
        List<AdminBrandDto> dtoList = brandMapper.toAdminBrandDto(brandList);

        return new PageImpl<>(dtoList, pageable, brandPage.getTotalElements());
    }

    public Brand getBrandById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));
    }

    private boolean existsByName(String name) {
        return brandRepository.existsBrandByName(name);
    }
}

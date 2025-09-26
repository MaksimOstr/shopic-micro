package com.productservice.services;

import com.productservice.dto.UserBrandDto;
import com.productservice.dto.request.AdminBrandParams;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.exceptions.AlreadyExistsException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.BrandMapper;
import com.productservice.repository.BrandRepository;
import com.productservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.productservice.utils.SpecificationUtils.equalsBoolean;


@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public Brand getBrandById(int id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));
    }

    @Transactional
    public Brand updateBrand(int id, UpdateBrandRequest dto) {
        Brand brand = getBrandById(id);

        Optional.ofNullable(dto.brandName()).ifPresent(brand::setName);

        return brand;
    }

    public Page<Brand> getAllBrands(AdminBrandParams params, Pageable pageable) {
        Specification<Brand> spec = SpecificationUtils.<Brand>iLike("name", params.name())
                .and(equalsBoolean("isActive", params.isActive()));

        return brandRepository.findAll(spec, pageable);
    }

    public Page<UserBrandDto> getUserBrandDtoPage(String name, Pageable pageable) {
        Specification<Brand> spec = SpecificationUtils.<Brand>iLike("name", name)
                .and(equalsBoolean("isActive", true));
        Page<Brand> brandPage = brandRepository.findAll(spec, pageable);
        List<Brand> brandList = brandPage.getContent();
        List<UserBrandDto> dtoList = brandMapper.toUserBrandDtoList(brandList);

        return new PageImpl<>(dtoList, pageable, brandPage.getTotalElements());
    }

    public void activate(int id) {
        int updated = brandRepository.changeIsActive(id, true);

        if(updated == 0) {
            throw new NotFoundException("Brand not found");
        }
    }


    public Brand create(CreateBrandRequest dto) {
        if(existsByName(dto.brandName())) {
            throw new AlreadyExistsException("Brand name already exists");
        }
        Brand brand = Brand.builder()
                .name(dto.brandName())
                .isActive(dto.isActive())
                .build();

        return brandRepository.save(brand);
    }

    private boolean existsByName(String name) {
        return brandRepository.existsBrandByName(name);
    }
}

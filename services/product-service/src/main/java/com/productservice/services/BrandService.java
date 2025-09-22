package com.productservice.services;

import com.productservice.dto.request.AdminBrandParams;
import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.exceptions.AlreadyExistsException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.BrandRepository;
import com.productservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.productservice.utils.SpecificationUtils.equalsBoolean;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

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

    public Page<Brand> getAllActiveBrands(String name, Pageable pageable) {
        Specification<Brand> spec = SpecificationUtils.<Brand>iLike("name", name)
                .and(equalsBoolean("isActive", true));

        return brandRepository.findAll(spec, pageable);
    }

    public void changeIsActive(int id, boolean value) {
        int updated = brandRepository.changeIsActive(id, value);

        if(updated == 0) {
            throw new NotFoundException("Brand not found");
        }
    }

    public Brand create(CreateBrandRequest dto) {
        if(existsByName(dto.brandName())) {
            throw new AlreadyExistsException("Brand already exists");
        }
        Brand brand = new Brand(dto.brandName());

        return brandRepository.save(brand);
    }

    private boolean existsByName(String name) {
        return brandRepository.existsBrandByName(name);
    }
}

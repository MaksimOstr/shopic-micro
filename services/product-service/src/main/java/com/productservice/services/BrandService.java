package com.productservice.services;

import com.productservice.dto.request.CreateBrandRequest;
import com.productservice.dto.request.UpdateBrandRequest;
import com.productservice.entity.Brand;
import com.productservice.exceptions.AlreadyExistsException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand create(CreateBrandRequest dto) {
        if(existsByName(dto.brandName())) {
            throw new AlreadyExistsException("Brand already exists");
        }
        Brand brand = new Brand(dto.brandName());

        return brandRepository.save(brand);
    }

    public void delete(int id) {
        int delete = brandRepository.deleteBrandById(id);

        if (delete == 0) {
            throw new NotFoundException("Brand not found");
        }
    }

    private boolean existsByName(String name) {
        return brandRepository.existsBrandByName(name);
    }
}

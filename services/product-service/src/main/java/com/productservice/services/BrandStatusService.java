package com.productservice.services;

import com.productservice.dto.response.BrandDeactivationCheckResponse;
import com.productservice.dto.response.BrandDeactivationResponse;
import com.productservice.entity.Brand;
import com.productservice.entity.ProductStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandStatusService {
    private final ProductService productService;
    private final BrandService brandService;


    @Transactional
    public BrandDeactivationResponse deactivate(int id) {
        Brand brand = brandService.getBrandById(id);

        if(!brand.isActive()) {
            throw new IllegalStateException("Brand is already deactivated");
        }

        brand.setActive(false);

        int deactivatedCount = productService.deactivateByBrandId(id);
        String message = deactivatedCount + " products were deactivated";

        return new BrandDeactivationResponse(
                brand.getId(),
                brand.getName(),
                deactivatedCount,
                message
        );
    }

    public void activate(int id) {
        brandService.activate(id);
    }
}

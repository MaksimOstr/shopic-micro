package com.productservice.services;

import com.productservice.dto.response.BrandDeactivationCheckResponse;
import com.productservice.dto.response.BrandDeactivationResponse;
import com.productservice.entity.Brand;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.BrandRepository;
import com.productservice.services.products.ProductCommandService;
import com.productservice.services.products.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandStatusService {
    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;
    private final BrandRepository brandRepository;
    private final BrandService brandService;


    @Transactional
    public BrandDeactivationCheckResponse deactivationCheck(int id) {
        Brand brand = brandService.getBrandById(id);
        int activeCount = productQueryService.countProductsByBrandIdAndStatus(brand.getId(), ProductStatusEnum.ACTIVE);
        String message = "During the deactivation, " + activeCount + " products will be affected";

        return new BrandDeactivationCheckResponse(
                brand.getId(),
                brand.getName(),
                activeCount,
                message
        );
    }

    @Transactional
    public BrandDeactivationResponse deactivate(int id) {
        Brand brand = brandService.getBrandById(id);

        if(!brand.isActive()) {
            throw new IllegalStateException("Brand is already deactivated");
        }

        brand.setActive(false);

        int deactivatedCount = productCommandService.deactivateByBrandId(id);
        String message = deactivatedCount + " products were deactivated";

        return new BrandDeactivationResponse(
                brand.getId(),
                brand.getName(),
                deactivatedCount,
                message
        );
    }

    public void activate(int id) {
        int updated = brandRepository.changeIsActive(id, true);

        if(updated == 0) {
            throw new NotFoundException("Brand not found");
        }
    }
}

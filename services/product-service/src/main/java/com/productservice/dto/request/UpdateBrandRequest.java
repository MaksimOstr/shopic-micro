package com.productservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateBrandRequest(
        @Size(min = 2, max = 30)
        String brandName,

        @NotNull
        Boolean isActive
) {
}

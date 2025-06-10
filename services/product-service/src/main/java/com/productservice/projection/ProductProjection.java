package com.productservice.projection;

import java.util.UUID;

public interface ProductProjection {

    long getId();
    String getName();
    String getDescription();
    String getImage();
    String getPrice();
    UUID getSku();
    int getStockQuantity();
    Brand getBrand();
    Category getCategory();

    interface Category {
        String getName();
    }

    interface Brand {
        String getName();
    }
}

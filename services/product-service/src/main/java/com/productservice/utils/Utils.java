package com.productservice.utils;

import com.productservice.dto.request.ItemForReservation;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class Utils {
    public static UUID getUUID() {
        return UUID.randomUUID();
    }

    public static List<Long> extractIds(List<ItemForReservation> items) {
        return items.stream().map(ItemForReservation::productId).toList();
    }
}

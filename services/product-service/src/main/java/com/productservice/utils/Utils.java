package com.productservice.utils;

import com.productservice.dto.request.ItemForReservationDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class Utils {
    public static UUID getUUID() {
        return UUID.randomUUID();
    }

    public static List<UUID> extractIds(List<ItemForReservationDto> items) {
        return items.stream().map(ItemForReservationDto::productId).toList();
    }
}

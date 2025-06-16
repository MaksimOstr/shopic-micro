package com.productservice.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class Utils {
    public static UUID getUUID() {
        return UUID.randomUUID();
    }
}

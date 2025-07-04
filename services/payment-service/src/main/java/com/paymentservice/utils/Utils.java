package com.paymentservice.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class Utils {
    public static BigDecimal toSmallestUnit(BigDecimal value) {
        return value.multiply(new BigDecimal(100));
    }
}

package com.orderservice.utils;

import com.orderservice.exception.NotFoundException;
import com.shopic.grpc.cartservice.CartItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@UtilityClass
public class OrderUtils {
    public static List<Long> getProductIds(List<CartItem> orderCartItems) {
        return orderCartItems.stream().map(CartItem::getProductId).toList();
    }
}

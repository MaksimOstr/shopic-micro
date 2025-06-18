package com.orderservice.utils;

import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.ProductInfo;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class OrderUtils {

    public static BigDecimal calculateTotalPrice(Collection<ProductInfo> products) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        return products.stream().map(product -> new BigDecimal(product.getPrice())).reduce(totalPrice, BigDecimal::add);
    }

    public static List<Long> getProductIds(List<CartItem> orderCartItems) {
        return orderCartItems.stream().map(CartItem::getProductId).toList();
    }
}

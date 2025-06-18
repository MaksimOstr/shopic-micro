package com.orderservice.service.grpc;

import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.CheckAndReserveProductsRequest;
import com.shopic.grpc.productservice.CheckProductResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import com.shopic.grpc.productservice.ReservationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductGrpcService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;
    private final GrpcMapper grpcMapper;

    public CheckProductResponse checkAndReserveProduct(List<CartItem> cartItems) {
        List<ReservationItem> reservationItems = mapToReservationItems(cartItems);
        CheckAndReserveProductsRequest request = CheckAndReserveProductsRequest.newBuilder()
                .addAllReservationItems(reservationItems).build();

        return productGrpcService.checkAndReserveProducts(request);
    }

    private List<ReservationItem> mapToReservationItems(List<CartItem> cartItems) {
        return cartItems.stream().map(grpcMapper::toReservationItem).toList();
    }
}

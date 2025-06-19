package com.orderservice.service.grpc;

import com.orderservice.exception.InsufficientStockException;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.CheckAndReserveProductsRequest;
import com.shopic.grpc.productservice.CheckAndReserveProductResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import com.shopic.grpc.productservice.ReservationItem;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductGrpcService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    public CheckAndReserveProductResponse checkAndReserveProduct(List<CartItem> cartItems) {
        System.out.println(cartItems);
        List<ReservationItem> reservationItems = mapToReservationItems(cartItems);
        CheckAndReserveProductsRequest request = CheckAndReserveProductsRequest.newBuilder()
                .addAllReservationItems(reservationItems).build();

        try {
            return productGrpcService.checkAndReserveProducts(request);
        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case NOT_FOUND: throw new NotFoundException(e.getStatus().getDescription());
                case FAILED_PRECONDITION: throw new InsufficientStockException(e.getStatus().getDescription());
                default: throw e;
            }
        }
    }

    private List<ReservationItem> mapToReservationItems(List<CartItem> cartItems) {
        return cartItems.stream().map(item -> ReservationItem.newBuilder()
                .setProductId(item.getProductId())
                .setQuantity(item.getQuantity())
                .build()).toList();
    }
}

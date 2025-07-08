package com.orderservice.service.grpc;

import com.google.protobuf.Empty;
import com.orderservice.exception.InsufficientStockException;
import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.*;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class ProductGrpcService {
    private final GrpcMapper grpcMapper;
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    public Empty reserveProduct(List<CartItem> cartItems, long orderId) {
        List<ReservationItem> reservationItems = grpcMapper.toReservationItemList(cartItems);
        ReserveProductsRequest request = ReserveProductsRequest.newBuilder()
                .setOrderId(orderId)
                .addAllReservationItems(reservationItems).build();

        try {
            return productGrpcService.reserveProducts(request);
        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case NOT_FOUND: throw new NotFoundException(e.getStatus().getDescription());
                case FAILED_PRECONDITION: throw new InsufficientStockException(e.getStatus().getDescription());
                default: throw e;
            }
        }
    }

    public ActualProductInfoResponse getActualProductInfo(List<Long> productIds) {
        GetActualProductInfoRequest request = GetActualProductInfoRequest.newBuilder()
                .addAllProductId(productIds).build();

        return productGrpcService.getActualProductInfo(request);
    }
}

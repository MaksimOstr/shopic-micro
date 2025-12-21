package com.orderservice.service.grpc;

import com.google.protobuf.Empty;
import com.orderservice.exception.ExternalServiceUnavailableException;
import com.orderservice.exception.InsufficientStockException;
import com.orderservice.exception.NotFoundException;
import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProductGrpcService {
    private final GrpcMapper grpcMapper;
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    @CircuitBreaker(name = "product-service", fallbackMethod = "reserveProductFallback")
    public Empty reserveProduct(List<CartItem> cartItems, UUID orderId) {
        List<ReservationItem> reservationItems = grpcMapper.toReservationItemList(cartItems);
        ReserveProductsRequest request = ReserveProductsRequest.newBuilder()
                .setOrderId(orderId.toString())
                .addAllReservationItems(reservationItems).build();

        return productGrpcService.reserveProducts(request);
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductListFallback")
    public ProductListResponse getProductList(List<String> productIds) {
        GetProductListRequest request = GetProductListRequest.newBuilder()
                .addAllProductId(productIds).build();

        return productGrpcService.getProductList(request);
    }

    public Empty reserveProductFallback(List<CartItem> cartItems, UUID orderId, Throwable throwable) {
        log.error("reserveProductFallback", throwable);

        if (throwable instanceof StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case NOT_FOUND:
                    throw new NotFoundException(e.getStatus().getDescription());
                case FAILED_PRECONDITION:
                    throw new InsufficientStockException(e.getStatus().getDescription());
                default:
                    throw e;
            }
        } else {
            throw new ExternalServiceUnavailableException("Something went wrong. Try again later");
        }
    }

    public ProductListResponse getProductListFallback(List<String> productIds, Throwable throwable) {
        log.error("getActualProductInfoFallback", throwable);
        throw new ExternalServiceUnavailableException("Something went wrong. Try again later");
    }
}

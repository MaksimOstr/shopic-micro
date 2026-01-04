package com.orderservice.service.grpc;

import com.google.protobuf.Empty;
import com.orderservice.exception.ApiException;
import com.orderservice.exception.ExternalServiceBusinessException;
import com.orderservice.exception.NotFoundException;
import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProductGrpcService {
    private final GrpcMapper grpcMapper;
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    @CircuitBreaker(name = "product-service", fallbackMethod = "reserveProductFallback")
    public ReserveProductsResponse reserveProducts(List<CartItem> cartItems, Map<String, CartItem> cartItemMap, UUID orderId) {
        log.info("Sending gRPC request to reserve products for orderId={} with {} items", orderId, cartItems.size());

        List<ReservationItem> reservationItems = grpcMapper.toReservationItemList(cartItems);
        ReserveProductsRequest request = ReserveProductsRequest.newBuilder()
                .setOrderId(orderId.toString())
                .addAllReservationItems(reservationItems).build();

        ReserveProductsResponse response = productGrpcService.reserveProducts(request);

        if (!response.getErrorsList().isEmpty()) {
            handleReservationErrors(response.getErrorsList(), cartItemMap);
        }

        return response;
    }

    public ReserveProductsResponse reserveProductFallback(List<CartItem> cartItems, Map<String, CartItem> cartItemMap, UUID orderId, Throwable throwable) {
        log.error("Product service fallback for orderId={}", orderId, throwable);

        if(throwable instanceof ExternalServiceBusinessException e) {
            throw e;
        }

        throw new ApiException("Something went wrong, try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }

    private void handleReservationErrors(
            List<ReservationError> errors,
            Map<String, CartItem> cartItemMap
    ) {
        if (errors.isEmpty()) return;

        List<String> errorMessages = errors.stream()
                .map(err -> {
                    CartItem cartItem = cartItemMap.get(err.getProductId());
                    String productName = cartItem != null ? cartItem.getProductName() : "Unknown Product";

                    return switch (err.getType()) {
                        case INSUFFICIENT_STOCK -> String.format(
                                "Product %s (%s) has insufficient stock. Requested: %d, Available: %d",
                                err.getProductId(),
                                productName,
                                err.getRequestedQuantity(),
                                err.getAvailableQuantity()
                        );
                        case NOT_FOUND -> String.format(
                                "Product %s (%s) is not available or inactive",
                                err.getProductId(),
                                productName
                        );
                        default -> String.format(
                                "Product %s (%s) reservation failed with error type: %s",
                                err.getProductId(),
                                productName,
                                err.getType()
                        );
                    };
                })
                .toList();

        String finalMessage = String.join("; ", errorMessages);
        throw new ExternalServiceBusinessException(finalMessage, HttpStatus.CONFLICT);
    }
}

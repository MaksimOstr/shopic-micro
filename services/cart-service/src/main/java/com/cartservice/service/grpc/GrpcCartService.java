package com.cartservice.service.grpc;

import com.cartservice.dto.CartItemDto;
import com.cartservice.mapper.GrpcMapper;
import com.cartservice.service.CartService;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.GetCartRequest;
import com.shopic.grpc.cartservice.CartResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcCartService extends CartServiceGrpc.CartServiceImplBase {
    private final CartService cartService;
    private final GrpcMapper grpcMapper;


    @Override
    public void getCart(GetCartRequest request, StreamObserver<CartResponse> responseObserver) {
        log.info("gRpc getCart request for user id: ${}", request.getUserId());
        UUID userId = UUID.fromString(request.getUserId());
        List<CartItemDto> cartItems = cartService.getCartItemsForOrder(userId);
        List<CartItem> cartItemList = grpcMapper.toOrderCartItems(cartItems);

        CartResponse response = CartResponse.newBuilder()
                .addAllCartItems(cartItemList)
                .build();

        log.info("gRpc getCart response for user id: ${} found ${} items", userId, cartItemList.size());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

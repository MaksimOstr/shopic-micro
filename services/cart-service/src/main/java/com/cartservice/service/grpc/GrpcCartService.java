package com.cartservice.service.grpc;

import com.cartservice.dto.CartItemDtoForOrder;
import com.cartservice.mapper.GrpcMapper;
import com.cartservice.service.CartService;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.GetCartRequest;
import com.shopic.grpc.cartservice.CartResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;


@GrpcService
@RequiredArgsConstructor
public class GrpcCartService extends CartServiceGrpc.CartServiceImplBase {
    private final CartService cartService;
    private final GrpcMapper grpcMapper;


    @Override
    public void getCart(GetCartRequest request, StreamObserver<CartResponse> responseObserver) {
        UUID userId = UUID.fromString(request.getUserId());
        List<CartItemDtoForOrder> cartItems = cartService.getCartItemsForOrder(userId);
        List<CartItem> cartItemList = grpcMapper.toOrderCartItems(cartItems);

        CartResponse response = CartResponse.newBuilder()
                .addAllCartItems(cartItemList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

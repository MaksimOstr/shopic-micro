package com.cartservice.service.grpc;

import com.cartservice.mapper.GrpcMapper;
import com.cartservice.projection.CartItemForOrderProjection;
import com.cartservice.service.CartService;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.GetCartRequest;
import com.shopic.grpc.cartservice.CartResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrpcCartService extends CartServiceGrpc.CartServiceImplBase {
    private final CartService cartService;
    private final GrpcMapper grpcMapper;


    @Override
    public void getCart(GetCartRequest request, StreamObserver<CartResponse> responseObserver) {
        List<CartItemForOrderProjection> cartItems = cartService.getCartItemsForOrder(request.getUserId());
        List<CartItem> cartItemList = mapToOrderCartItem(cartItems);

        CartResponse response = CartResponse.newBuilder()
                .addAllCartItems(cartItemList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private List<CartItem> mapToOrderCartItem(List<CartItemForOrderProjection> cartItems) {
        return cartItems.stream()
                .map(grpcMapper::toOrderCartItem)
                .toList();
    }
}

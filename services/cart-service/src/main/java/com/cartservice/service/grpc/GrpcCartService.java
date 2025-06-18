package com.cartservice.service.grpc;

import com.cartservice.mapper.GrpcMapper;
import com.cartservice.projection.CartItemForOrderProjection;
import com.cartservice.service.CartService;
import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.OrderCartInfoGrpcRequest;
import com.shopic.grpc.cartservice.OrderCartInfoGrpcResponse;
import com.shopic.grpc.cartservice.OrderCartItem;
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
    public void getOrderCartInfo(OrderCartInfoGrpcRequest request, StreamObserver<OrderCartInfoGrpcResponse> responseObserver) {
        List<CartItemForOrderProjection> cartItems = cartService.getCartItemsForOrder(request.getUserId());
        List<OrderCartItem> orderCartItems = mapToOrderCartItem(cartItems);

        OrderCartInfoGrpcResponse response = OrderCartInfoGrpcResponse.newBuilder()
                .addAllOrderCartItems(orderCartItems)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private List<OrderCartItem> mapToOrderCartItem(List<CartItemForOrderProjection> cartItems) {
        return cartItems.stream()
                .map(grpcMapper::toOrderCartItem)
                .toList();
    }
}

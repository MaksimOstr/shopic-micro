package com.orderservice.service.grpc;

import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.GetCartRequest;
import com.shopic.grpc.cartservice.CartResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartGrpcService {
    private final CartServiceGrpc.CartServiceBlockingStub cartGrpcService;

    public CartResponse getCartInfo(long userId) {
        GetCartRequest request = GetCartRequest.newBuilder()
                .setUserId(userId)
                .build();

        try {
            return cartGrpcService.getCart(request);
        } catch (StatusRuntimeException e) {
            if(e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException(e.getStatus().getDescription());
            }
            throw e;
        }
    }

}

package com.productservice.services.grpc;

import com.productservice.exceptions.ProductStockUnavailableException;
import com.productservice.mapper.GrpcMapper;
import com.productservice.projection.ProductForCartDto;
import com.productservice.services.ProductService;
import com.shopic.grpc.productservice.CartItemAddGrpcRequest;
import com.shopic.grpc.productservice.CartItemAddGrpcResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GrpcProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;
    private final GrpcMapper grpcMapper;

    @Override
    public void getProductInfoForCart(CartItemAddGrpcRequest request, StreamObserver<CartItemAddGrpcResponse> responseObserver) {
        ProductForCartDto productDto = productService.getProductInfoForCart(request.getProductId());

        if(productDto.stockQuantity() < request.getQuantity()) {
            throw new ProductStockUnavailableException("Insufficient stock");
        }

        CartItemAddGrpcResponse response = grpcMapper.toCartItemAddGrpcResponse(productDto);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

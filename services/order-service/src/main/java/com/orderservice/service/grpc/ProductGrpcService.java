package com.orderservice.service.grpc;

import com.shopic.grpc.productservice.CheckProductGrpcRequest;
import com.shopic.grpc.productservice.CheckProductGrpcResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductGrpcService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    public CheckProductGrpcResponse checkProducts(List<Long> productIds) {
        CheckProductGrpcRequest request = CheckProductGrpcRequest.newBuilder()
                .addAllProductId(productIds).build();

        return productGrpcService.checkActualProductPriceAndStock(request);
    }
}

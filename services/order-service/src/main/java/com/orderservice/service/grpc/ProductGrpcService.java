package com.orderservice.service.grpc;

import com.shopic.grpc.productservice.GetProductInfoBatchRequest;
import com.shopic.grpc.productservice.GetProductInfoBatchResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductGrpcService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    public GetProductInfoBatchResponse getProductInfoBatch(List<Long> productIds) {
        GetProductInfoBatchRequest request = GetProductInfoBatchRequest.newBuilder()
                .addAllProductIds(productIds).build();

        return productGrpcService.getProductInfoBatch(request);
    }
}

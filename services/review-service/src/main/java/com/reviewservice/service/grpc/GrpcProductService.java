package com.reviewservice.service.grpc;


import com.shopic.grpc.productservice.IsProductExistsRequest;
import com.shopic.grpc.productservice.IsProductExistsResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcProductService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    public IsProductExistsResponse isProductExists(long productId) {
        IsProductExistsRequest request = IsProductExistsRequest.newBuilder()
                .setProductId(productId)
                .build();

        return productGrpcService.isProductExists(request);
    }


}

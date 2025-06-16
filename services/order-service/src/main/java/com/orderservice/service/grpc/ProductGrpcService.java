package com.orderservice.service.grpc;

import com.shopic.grpc.productservice.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGrpcService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;
}

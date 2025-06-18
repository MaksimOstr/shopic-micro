package com.cartservice.service.grpc;

import com.cartservice.exception.InsufficientProductStockException;
import com.cartservice.exception.NotFoundException;
import com.shopic.grpc.productservice.GetProductDetailsRequest;
import com.shopic.grpc.productservice.ProductDetailsResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GrpcProductService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceGrpc;

    public ProductDetailsResponse getProductInfoForCart(long productId, int quantity) {
        GetProductDetailsRequest request = GetProductDetailsRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        try {
            return productServiceGrpc.getProductPriceAndStock(request);
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();

            switch (code) {
                case NOT_FOUND -> throw new NotFoundException(e.getStatus().getDescription());
                case FAILED_PRECONDITION -> throw new InsufficientProductStockException(e.getStatus().getDescription());
                default -> throw e;
            }
        }
    }
}

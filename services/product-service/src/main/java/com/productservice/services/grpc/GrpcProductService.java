package com.productservice.services.grpc;

import com.productservice.exceptions.ProductStockUnavailableException;
import com.productservice.mapper.GrpcMapper;
import com.productservice.projection.ProductPriceAndQuantityDto;
import com.productservice.services.ProductService;
import com.shopic.grpc.productservice.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GrpcProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;
    private final GrpcMapper grpcMapper;

    @Override
    public void getProductInfoForCart(CartItemAddGrpcRequest request, StreamObserver<CartItemAddGrpcResponse> responseObserver) {
        ProductPriceAndQuantityDto productDto = productService.getProductInfoForCart(request.getProductId());

        if(productDto.stockQuantity() < request.getQuantity()) {
            throw new ProductStockUnavailableException("Insufficient stock");
        }

        CartItemAddGrpcResponse response = grpcMapper.toCartItemAddGrpcResponse(productDto);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkActualProductPriceAndStock(CheckProductGrpcRequest request, StreamObserver<CheckProductGrpcResponse> responseObserver) {
        List<ProductPriceAndQuantityDto> productPriceAndQuantityList = productService.getProductPriceAndQuantity(request.getProductIdList());
        List<ProductInfoForOrder> productInfoForOrderList = mapProductInfoForOrder(productPriceAndQuantityList);

        CheckProductGrpcResponse response = CheckProductGrpcResponse.newBuilder()
                .addAllProductInfoForOrderList(productInfoForOrderList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private List<ProductInfoForOrder> mapProductInfoForOrder(List<ProductPriceAndQuantityDto> productInfoForOrderList) {
        return productInfoForOrderList.stream()
                .map(grpcMapper::toProductInfoForOrder)
                .toList();
    }
}

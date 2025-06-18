package com.productservice.services.grpc;

import com.productservice.exceptions.ProductStockUnavailableException;
import com.productservice.mapper.GrpcMapper;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductForOrderDto;
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
    public void getProductPriceAndStock(GetProductDetailsRequest request, StreamObserver<ProductDetailsResponse> responseObserver) {
        ProductForCartDto productDto = productService.getProductInfoForCart(request.getProductId());

        if(productDto.stockQuantity() < request.getQuantity()) {
            throw new ProductStockUnavailableException("Insufficient stock");
        }

        ProductDetailsResponse response = grpcMapper.toCartItemAddGrpcResponse(productDto);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkAndReserveProducts(CheckAndReserveProductsRequest request, StreamObserver<CheckProductResponse> responseObserver) {
        List<ProductForOrderDto> productPriceAndQuantityList = productService.checkAndReserveProduct();
        List<ProductInfo> productInfoForOrderList = mapProductInfoForOrder(productPriceAndQuantityList);

        CheckProductResponse response = CheckProductResponse.newBuilder()
                .addAllProducts(productInfoForOrderList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private List<ProductInfo> mapProductInfoForOrder(List<ProductForOrderDto> productInfoForOrderList) {
        return productInfoForOrderList.stream()
                .map(grpcMapper::toProductInfo)
                .toList();
    }
}

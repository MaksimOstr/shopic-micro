package com.productservice.services.grpc;

import com.google.protobuf.Empty;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.mapper.GrpcMapper;
import com.productservice.mapper.ProductMapper;
import com.productservice.services.ProductService;
import com.productservice.services.ReservationService;
import com.shopic.grpc.productservice.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;


@GrpcService
@RequiredArgsConstructor
public class GrpcProductService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ReservationService reservationService;
    private final ProductService productService;
    private final GrpcMapper grpcMapper;
    private final ProductMapper productMapper;

    @Override
    public void getProductById(GetProductRequest request, StreamObserver<Product> responseObserver) {
        com.productservice.entity.Product product = productService.getActiveProductById(UUID.fromString(request.getProductId()));
        Product response = productMapper.toGrpcProduct(product);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reserveProducts(ReserveProductsRequest request, StreamObserver<Empty> responseObserver) {
        List<ItemForReservationDto> itemsForReservation = grpcMapper.toItemForReservationList(request.getReservationItemsList());

        reservationService.createReservation(itemsForReservation, UUID.fromString(request.getOrderId()));

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductList(GetProductListRequest request, StreamObserver<ProductListResponse> responseObserver) {
        List<UUID> mappedProductIds = request.getProductIdList().stream().map(UUID::fromString).toList();
        List<com.productservice.entity.Product> products = productService.getActiveProductsByIds(mappedProductIds);
        List<Product> productInfoList = productMapper.toGrpcProductList(products);

        ProductListResponse response = ProductListResponse.newBuilder()
                .addAllProducts(productInfoList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isProductExists(IsProductExistsRequest request, StreamObserver<IsProductExistsResponse> responseObserver) {
        boolean isExists = productService.existsById(UUID.fromString(request.getProductId()));

        IsProductExistsResponse response = IsProductExistsResponse.newBuilder()
                .setExists(isExists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

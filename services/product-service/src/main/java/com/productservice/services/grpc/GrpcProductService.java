package com.productservice.services.grpc;

import com.google.protobuf.Empty;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.dto.request.CreateReservationDto;
import com.productservice.mapper.GrpcMapper;
import com.productservice.dto.ProductBasicInfoDto;
import com.productservice.services.ReservationCreationService;
import com.productservice.services.products.ProductService;
import com.shopic.grpc.productservice.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;



@GrpcService
@RequiredArgsConstructor
public class GrpcProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;
    private final ReservationCreationService reservationCreationService;
    private final GrpcMapper grpcMapper;

    @Override
    public void getProductInfo(ProductInfoRequest request, StreamObserver<ProductInfo> responseObserver) {
        ProductBasicInfoDto productDto = productService.getActiveProductBasicInfo(request.getProductId());

        ProductInfo response = grpcMapper.toProductInfo(productDto);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reserveProducts(ReserveProductsRequest request, StreamObserver<Empty> responseObserver) {
        List<ItemForReservationDto> itemsForReservation = grpcMapper.toItemForReservationList(request.getReservationItemsList());
        CreateReservationDto dto = new CreateReservationDto(itemsForReservation, request.getOrderId());

        reservationCreationService.createReservation(dto);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductInfoList(ProductInfoListRequest request, StreamObserver<ProductInfoList> responseObserver) {
        List<ProductBasicInfoDto> productPrices = productService.getActiveProductBasicInfoList(request.getProductIdList());
        List<ProductInfo> productInfoList = grpcMapper.toProductInfoList(productPrices);
        ProductInfoList response = ProductInfoList.newBuilder()
                .addAllProducts(productInfoList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isProductExists(IsProductExistsRequest request, StreamObserver<IsProductExistsResponse> responseObserver) {
        boolean isExists = productService.existsById(request.getProductId());

        IsProductExistsResponse response = IsProductExistsResponse.newBuilder()
                .setIsExists(isExists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

package com.productservice.services.grpc;

import com.google.protobuf.Empty;
import com.productservice.dto.request.ItemForReservation;
import com.productservice.dto.request.CreateReservationDto;
import com.productservice.exceptions.InsufficientStockException;
import com.productservice.mapper.GrpcMapper;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductInfoDto;
import com.productservice.services.ReservationCreationService;
import com.productservice.services.products.ProductQueryService;
import com.shopic.grpc.productservice.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;



@GrpcService
@RequiredArgsConstructor
public class GrpcProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductQueryService productQueryService;
    private final ReservationCreationService reservationCreationService;
    private final GrpcMapper grpcMapper;

    @Override
    public void getProductPriceAndStock(GetProductDetailsRequest request, StreamObserver<ProductDetailsResponse> responseObserver) {
        ProductForCartDto productDto = productQueryService.getProductInfoForCart(request.getProductId());

        if(productDto.stockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }

        ProductDetailsResponse response = grpcMapper.toProductDetails(productDto);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reserveProducts(ReserveProductsRequest request, StreamObserver<Empty> responseObserver) {
        List<ItemForReservation> itemsForReservation = grpcMapper.toItemForReservationList(request.getReservationItemsList());
        CreateReservationDto dto = new CreateReservationDto(itemsForReservation, request.getOrderId());

        reservationCreationService.createReservation(dto);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getActualProductInfo(GetActualProductInfoRequest request, StreamObserver<ActualProductInfoResponse> responseObserver) {
        List<ProductInfoDto> productPrices = productQueryService.getProductInfo(request.getProductIdList());
        List<ProductInfo> productInfoList = productPrices.stream().map(grpcMapper::toProductInfo).toList();
        ActualProductInfoResponse response = ActualProductInfoResponse.newBuilder()
                .addAllProducts(productInfoList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isProductExists(IsProductExistsRequest request, StreamObserver<IsProductExistsResponse> responseObserver) {
        boolean isExists = productQueryService.isProductExist(request.getProductId());

        IsProductExistsResponse response = IsProductExistsResponse.newBuilder()
                .setIsExists(isExists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

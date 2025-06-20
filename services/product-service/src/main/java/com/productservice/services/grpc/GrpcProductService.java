package com.productservice.services.grpc;

import com.productservice.dto.request.ItemForReservation;
import com.productservice.dto.request.CreateReservationDto;
import com.productservice.exceptions.InsufficientStockException;
import com.productservice.mapper.GrpcMapper;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductPrice;
import com.productservice.services.ReservationCreationService;
import com.productservice.services.products.ProductQueryService;
import com.shopic.grpc.productservice.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.productservice.utils.Utils.extractIds;


@Service
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
    @Transactional
    public void checkAndReserveProducts(CheckAndReserveProductsRequest request, StreamObserver<CheckAndReserveProductResponse > responseObserver) {
        List<ItemForReservation> itemsForReservation = mapToItemForReservation(request.getReservationItemsList());
        CreateReservationDto dto = new CreateReservationDto(itemsForReservation, request.getUserId());
        long reservationId = reservationCreationService.createReservation(dto);

        List<Long> productIds = extractIds(itemsForReservation);
        List<ProductPrice> productPrices = productQueryService.getProductPrices(productIds);
        List<ProductInfo> productInfoList = productPrices.stream().map(grpcMapper::toProductInfo).toList();

        CheckAndReserveProductResponse response = CheckAndReserveProductResponse.newBuilder()
                .addAllProducts(productInfoList)
                .setReservationId(reservationId)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private List<ItemForReservation> mapToItemForReservation(List<ReservationItem> reservationItems) {
        return reservationItems.stream().map(grpcMapper::toItemForReservation).toList();
    }
}

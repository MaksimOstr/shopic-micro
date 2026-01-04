package com.productservice.services.grpc;

import com.productservice.dto.ReservationResult;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.mapper.GrpcMapper;
import com.productservice.mapper.ProductMapper;
import com.productservice.mapper.ReservationErrorMapper;
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
    private final ReservationErrorMapper reservationErrorMapper;

    @Override
    public void getProductById(GetProductRequest request, StreamObserver<Product> responseObserver) {
        com.productservice.entity.Product product = productService.getActiveWithCategoryAndBrandById(UUID.fromString(request.getProductId()));
        long availableQuantity = reservationService.getAvailableQuantityByProductId(product.getId());
        Product response = productMapper.toGrpcProduct(product, availableQuantity);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reserveProducts(ReserveProductsRequest request, StreamObserver<ReserveProductsResponse> responseObserver) {
        List<ItemForReservationDto> itemsForReservation = grpcMapper.toItemForReservationList(request.getReservationItemsList());
        ReservationResult result = reservationService.createReservation(itemsForReservation, UUID.fromString(request.getOrderId()));
        List<ReservedProduct> reservedProducts = productMapper.toReservedProductList(result.reservedProducts());
        List<ReservationError> errors = reservationErrorMapper.toGrpcReservationErrorList(result.errors());

        ReserveProductsResponse response = ReserveProductsResponse.newBuilder()
                .addAllProducts(reservedProducts)
                .addAllErrors(errors)
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

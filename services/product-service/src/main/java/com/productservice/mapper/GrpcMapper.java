package com.productservice.mapper;

import com.productservice.dto.request.ItemForReservation;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductPrice;
import com.shopic.grpc.productservice.ProductDetailsResponse;
import com.shopic.grpc.productservice.ProductInfo;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    ProductDetailsResponse toProductDetails(ProductForCartDto dto);

    ProductInfo toProductInfo(ProductPrice dto);

    ItemForReservation toItemForReservation(ReservationItem reservationItem);
}

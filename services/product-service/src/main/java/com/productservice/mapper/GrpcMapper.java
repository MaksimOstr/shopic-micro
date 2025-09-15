package com.productservice.mapper;

import com.productservice.dto.request.ItemForReservation;
import com.productservice.dto.ProductBasicInfoDto;
import com.shopic.grpc.productservice.ProductInfo;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    ProductInfo toProductInfo(ProductBasicInfoDto dto);

    ItemForReservation toItemForReservation(ReservationItem reservationItem);

    List<ItemForReservation> toItemForReservationList(List<ReservationItem> reservationItemList);
}

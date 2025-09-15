package com.productservice.mapper;

import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.dto.ProductBasicInfoDto;
import com.shopic.grpc.productservice.ProductInfo;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    ProductInfo toProductInfo(ProductBasicInfoDto dto);

    List<ProductInfo> toProductInfoList(List<ProductBasicInfoDto> dtoList);

    ItemForReservationDto toItemForReservation(ReservationItem reservationItem);

    List<ItemForReservationDto> toItemForReservationList(List<ReservationItem> reservationItemList);
}

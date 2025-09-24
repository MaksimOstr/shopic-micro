package com.productservice.mapper;

import com.productservice.dto.AdminReservationItemDto;
import com.productservice.dto.request.CreateReservationItem;
import com.productservice.entity.ReservationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationItemMapper {

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "reservation.id", source = "reservationId")
    ReservationItem toReservationItem(CreateReservationItem item);

    List<ReservationItem> toReservationItemList(List<CreateReservationItem> item);

    AdminReservationItemDto toAdminReservationItemDto(ReservationItem reservation);

    List<AdminReservationItemDto> toAdminReservationItemDtoList(List<ReservationItem> itemList);
}

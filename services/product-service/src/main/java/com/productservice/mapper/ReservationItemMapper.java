package com.productservice.mapper;

import com.productservice.dto.ReservationItemDto;
import com.productservice.dto.request.CreateReservationItem;
import com.productservice.entity.ReservationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReservationItemMapper {

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "reservation.id", source = "reservationId")
    ReservationItem toReservationItem(CreateReservationItem item);

    List<ReservationItem> toReservationItemList(List<CreateReservationItem> item);

    ReservationItemDto toAdminReservationItemDto(ReservationItem reservation);

    List<ReservationItemDto> toAdminReservationItemDtoList(List<ReservationItem> itemList);

    default List<UUID> mapToProductIds(List<ReservationItem> items) {
        return items.stream().map(item -> item.getProduct().getId()).collect(Collectors.toList());
    }
}

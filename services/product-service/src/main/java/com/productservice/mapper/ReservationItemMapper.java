package com.productservice.mapper;

import com.productservice.dto.request.CreateReservationItem;
import com.productservice.entity.ReservationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationItemMapper {

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "reservation.id", source = "reservationId")
    ReservationItem toReservationItem(CreateReservationItem item);
}

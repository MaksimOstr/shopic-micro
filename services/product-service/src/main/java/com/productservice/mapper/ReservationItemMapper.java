package com.productservice.mapper;

import com.productservice.dto.request.CreateReservationItem;
import com.productservice.entity.ReservationItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationItemMapper {

    ReservationItem toReservationItem(CreateReservationItem item);
}

package com.productservice.mapper;


import com.productservice.dto.request.CreateReservationItem;
import com.productservice.dto.request.ItemForReservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

     CreateReservationItem toCreateReservationItem(ItemForReservation reservation);
}

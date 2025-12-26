package com.productservice.mapper;

import com.productservice.dto.ReservationDto;
import com.productservice.dto.ReservationPreviewDto;
import com.productservice.entity.Reservation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReservationItemMapper.class})
public interface ReservationMapper {
    ReservationDto toAdminReservationDto(Reservation reservation);

    ReservationPreviewDto toAdminReservationPreviewDto(Reservation reservation);

    List<ReservationPreviewDto> toAdminReservationPreviewDtoList(List<Reservation> reservationList);
}

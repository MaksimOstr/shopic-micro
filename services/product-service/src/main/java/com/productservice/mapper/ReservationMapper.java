package com.productservice.mapper;

import com.productservice.dto.AdminReservationDto;
import com.productservice.dto.AdminReservationPreviewDto;
import com.productservice.entity.Reservation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReservationItemMapper.class})
public interface ReservationMapper {
    AdminReservationDto toAdminReservationDto(Reservation reservation);

    AdminReservationPreviewDto toAdminReservationPreviewDto(Reservation reservation);

    List<AdminReservationPreviewDto> toAdminReservationPreviewDtoList(List<Reservation> reservationList);
}

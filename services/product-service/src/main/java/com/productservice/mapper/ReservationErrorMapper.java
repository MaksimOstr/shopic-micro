package com.productservice.mapper;

import com.shopic.grpc.productservice.ReservationError;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationErrorMapper {

    ReservationError toGrpcReservationError(com.productservice.dto.ReservationError error);

    List<ReservationError> toGrpcReservationErrorList(List<com.productservice.dto.ReservationError> errors);
}

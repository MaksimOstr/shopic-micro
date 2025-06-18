package com.productservice.services;

import com.productservice.dto.request.CreateReservationDto;
import com.productservice.entity.Reservation;
import com.productservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;


    public void createReservation(CreateReservationDto dto) {

    }

}

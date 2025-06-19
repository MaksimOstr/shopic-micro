package com.productservice.services;

import com.productservice.dto.request.CreateReservationItem;
import com.productservice.dto.request.ItemForReservation;
import com.productservice.entity.ReservationItem;
import com.productservice.mapper.ReservationItemMapper;
import com.productservice.repository.ReservationItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationItemService {
    private final ReservationItemRepository reservationItemRepository;
    private final ReservationItemMapper reservationItemMapper;

    public void saveReservationItems(List<CreateReservationItem> reservationItems) {
        List<ReservationItem> reservationItemList = reservationItems.stream().map(reservationItemMapper::toReservationItem).toList();

        reservationItemRepository.saveAll(reservationItemList);
    }


}

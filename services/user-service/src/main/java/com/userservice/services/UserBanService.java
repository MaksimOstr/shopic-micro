package com.userservice.services;

import com.userservice.repositories.BanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBanService {
    private final BanRepository banRepository;


}

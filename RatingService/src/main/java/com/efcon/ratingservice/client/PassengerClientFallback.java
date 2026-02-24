package com.efcon.ratingservice.client;

import com.efcon.ratingservice.client.dto.ExistenceResponseDto;
import com.efcon.ratingservice.rating.service.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class PassengerClientFallback implements PassengerClient {

    @Override
    public ExistenceResponseDto existsById(Long id) {
        throw new ServiceUnavailableException("PassengerService is temporarily unavailable");
    }
}
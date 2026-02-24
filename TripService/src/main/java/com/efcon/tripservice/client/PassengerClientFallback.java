package com.efcon.tripservice.client;

import com.efcon.tripservice.client.dto.ExistenceResponseDto;
import com.efcon.tripservice.service.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class PassengerClientFallback implements PassengerClient {

    @Override
    public ExistenceResponseDto existsById(Long id) {
        throw new ServiceUnavailableException("PassengerService is temporarily unavailable");
    }
}
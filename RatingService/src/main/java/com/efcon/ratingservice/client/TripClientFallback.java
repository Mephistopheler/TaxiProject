package com.efcon.ratingservice.client;

import com.efcon.ratingservice.client.dto.ExistenceResponseDto;
import com.efcon.ratingservice.rating.service.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class TripClientFallback implements TripClient {

    @Override
    public ExistenceResponseDto existsById(String id) {
        throw new ServiceUnavailableException("TripService is temporarily unavailable");
    }
}
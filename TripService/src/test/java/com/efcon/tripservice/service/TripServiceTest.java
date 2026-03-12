package com.efcon.tripservice.service;

import com.efcon.tripservice.dto.TripRequestDto;
import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.mapper.TripMapper;
import com.efcon.tripservice.messaging.TripStatusEventProducer;
import com.efcon.tripservice.model.Trip;
import com.efcon.tripservice.repository.TripRepository;
import com.efcon.tripservice.saga.SagaExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @Mock TripRepository tripRepository;
    @Mock TripMapper tripMapper;
    @Mock TripValidationService tripValidationService;
    @Mock SagaExecutor sagaExecutor;
    @Mock TripStatusEventProducer tripStatusEventProducer;
    @InjectMocks TripService tripService;

    @Test
    void create_returnsMappedResponse() {
        TripRequestDto req = new TripRequestDto();
        req.setDriverId(1L); req.setPassengerId(2L); req.setPickupAddress("A"); req.setDestinationAddress("B"); req.setCost(BigDecimal.TEN);
        Trip trip = new Trip();
        TripResponseDto resp = new TripResponseDto();
        when(tripMapper.toEntity(req)).thenReturn(trip);
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);
        when(tripMapper.toDto(any(Trip.class))).thenReturn(resp);

        TripResponseDto result = tripService.create(req);

        assertEquals(resp, result);
        verify(sagaExecutor).execute(any(), any());
    }
}
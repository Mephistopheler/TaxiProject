package com.efcon.tripservice.service;

import com.efcon.tripservice.dto.TripRequestDto;
import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.mapper.TripMapper;
import com.efcon.tripservice.messaging.TripStatusEventProducer;
import com.efcon.tripservice.model.Trip;
import com.efcon.tripservice.model.TripStatus;
import com.efcon.tripservice.repository.TripRepository;
import com.efcon.tripservice.saga.SagaExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
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
    @Test
    void updateStatus_allowsOnlyConfiguredForwardTransition() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.CREATED);
        Trip savedTrip = new Trip();
        savedTrip.setId("trip-1");
        savedTrip.setStatus(TripStatus.ACCEPTED);
        TripResponseDto responseDto = new TripResponseDto();

        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(savedTrip);
        when(tripMapper.toDto(savedTrip)).thenReturn(responseDto);

        TripResponseDto result = tripService.updateStatus("trip-1", TripStatus.ACCEPTED);

        assertEquals(responseDto, result);
        verify(tripRepository).save(argThat(updatedTrip -> updatedTrip.getStatus() == TripStatus.ACCEPTED));
        verify(tripStatusEventProducer).publish(any());
    }

    @Test
    void updateStatus_rejectsSkippingStates() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.CREATED);
        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> tripService.updateStatus("trip-1", TripStatus.COMPLETED));

        assertEquals("Invalid status transition: CREATED -> COMPLETED", exception.getMessage());
        verify(tripRepository, never()).save(any());
        verify(tripStatusEventProducer, never()).publish(any());
    }

    @Test
    void updateStatus_rejectsTerminalStatusChanges() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.COMPLETED);
        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> tripService.updateStatus("trip-1", TripStatus.CANCELED));

        assertEquals("Invalid status transition: COMPLETED -> CANCELED", exception.getMessage());
        verify(tripRepository, never()).save(any());
        verify(tripStatusEventProducer, never()).publish(any());
    }

    @Test
    void updateStatus_allowsIdempotentUpdate() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.ACCEPTED);
        TripResponseDto responseDto = new TripResponseDto();

        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tripMapper.toDto(any(Trip.class))).thenReturn(responseDto);

        TripResponseDto result = tripService.updateStatus("trip-1", TripStatus.ACCEPTED);

        assertEquals(responseDto, result);
        verify(tripRepository).save(argThat(updatedTrip -> updatedTrip.getStatus() == TripStatus.ACCEPTED));
        verify(tripStatusEventProducer).publish(any());
    }
}
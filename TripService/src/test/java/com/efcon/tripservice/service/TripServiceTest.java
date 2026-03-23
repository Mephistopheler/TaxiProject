package com.efcon.tripservice.service;

import com.efcon.tripservice.dto.TripRequestDto;
import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.mapper.TripMapper;
import com.efcon.tripservice.messaging.TripStatusEventProducer;
import com.efcon.tripservice.model.PendingTripStatusEvent;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @Mock TripRepository tripRepository;
    @Mock TripMapper tripMapper;
    @Mock TripValidationService tripValidationService;
    @Mock SagaExecutor sagaExecutor;
    @Mock TripStatusPublicationService tripStatusPublicationService;
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
    void updateStatus_savesPendingEventForForwardTransition() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.CREATED);
        Trip savedTrip = new Trip();
        savedTrip.setId("trip-1");
        savedTrip.setStatus(TripStatus.ACCEPTED);
        TripResponseDto responseDto = new TripResponseDto();

        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip), Optional.of(savedTrip));
        when(tripRepository.save(any(Trip.class))).thenReturn(savedTrip);
        when(tripMapper.toDto(savedTrip)).thenReturn(responseDto);

        TripResponseDto result = tripService.updateStatus("trip-1", TripStatus.ACCEPTED);

        assertEquals(responseDto, result);
        verify(tripRepository).save(argThat(updatedTrip -> updatedTrip.getStatus() == TripStatus.ACCEPTED
                && updatedTrip.getPendingStatusEvent() != null));
        verify(tripStatusPublicationService).publishPendingEventOrThrow(savedTrip);
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
        verify(tripStatusPublicationService, never()).publishPendingEventOrThrow(any());
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
        verify(tripStatusPublicationService, never()).publishPendingEventOrThrow(any());
    }

    @Test
    void updateStatus_allowsIdempotentUpdateWithoutPendingEvent() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.ACCEPTED);
        TripResponseDto responseDto = new TripResponseDto();

        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));
        when(tripMapper.toDto(trip)).thenReturn(responseDto);

        TripResponseDto result = tripService.updateStatus("trip-1", TripStatus.ACCEPTED);

        assertEquals(responseDto, result);
        verify(tripRepository, never()).save(any(Trip.class));
        verify(tripStatusPublicationService, never()).publishPendingEventOrThrow(any());
    }

    @Test
    void updateStatus_retriesPendingEventForSameStatus() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.COMPLETED);
        trip.setPendingStatusEvent(PendingTripStatusEvent.forStatus(TripStatus.COMPLETED));
        TripResponseDto responseDto = new TripResponseDto();

        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip), Optional.of(trip));
        when(tripMapper.toDto(trip)).thenReturn(responseDto);

        TripResponseDto result = tripService.updateStatus("trip-1", TripStatus.COMPLETED);

        assertEquals(responseDto, result);
        verify(tripRepository, never()).save(any(Trip.class));
        verify(tripStatusPublicationService).publishPendingEventOrThrow(trip);
    }

    @Test
    void updateStatus_keepsPendingEventWhenPublishFails() {
        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.CREATED);

        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("kafka down")).when(tripStatusPublicationService).publishPendingEventOrThrow(any());

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> tripService.updateStatus("trip-1", TripStatus.ACCEPTED));

        assertEquals("kafka down", exception.getMessage());
        verify(tripRepository).save(argThat(updatedTrip -> {
            assertEquals(TripStatus.ACCEPTED, updatedTrip.getStatus());
            assertNotNull(updatedTrip.getPendingStatusEvent());
            assertEquals(TripStatus.ACCEPTED, updatedTrip.getPendingStatusEvent().getStatus());
            return true;
        }));
    }
    @Test
    void update_rejectsEditingCompletedTrip() {
        TripRequestDto req = new TripRequestDto();
        req.setDriverId(10L);
        req.setPassengerId(20L);
        req.setPickupAddress("A");
        req.setDestinationAddress("B");
        req.setCost(BigDecimal.ONE);

        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.COMPLETED);
        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> tripService.update("trip-1", req));

        assertEquals("Trip is not editable in status: COMPLETED", exception.getMessage());
        verify(tripValidationService, never()).validateReferences(any(), any());
        verify(tripRepository, never()).save(any());
    }

    @Test
    void update_rejectsEditingCanceledTrip() {
        TripRequestDto req = new TripRequestDto();
        req.setDriverId(10L);
        req.setPassengerId(20L);
        req.setPickupAddress("A");
        req.setDestinationAddress("B");
        req.setCost(BigDecimal.ONE);

        Trip trip = new Trip();
        trip.setId("trip-1");
        trip.setStatus(TripStatus.CANCELED);
        when(tripRepository.findById("trip-1")).thenReturn(Optional.of(trip));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> tripService.update("trip-1", req));

        assertEquals("Trip is not editable in status: CANCELED", exception.getMessage());
        verify(tripValidationService, never()).validateReferences(any(), any());
        verify(tripRepository, never()).save(any());
    }

}

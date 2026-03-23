package com.efcon.tripservice.service;


import com.efcon.tripservice.dto.TripRequestDto;
import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.mapper.TripMapper;
import com.efcon.tripservice.messaging.TripStatusChangedEvent;
import com.efcon.tripservice.messaging.TripStatusEventProducer;
import com.efcon.tripservice.model.PendingTripStatusEvent;
import com.efcon.tripservice.model.SagaStatus;
import com.efcon.tripservice.model.Trip;
import com.efcon.tripservice.model.TripStatus;
import com.efcon.tripservice.repository.TripRepository;
import com.efcon.tripservice.saga.SagaExecutionException;
import com.efcon.tripservice.saga.SagaExecutor;
import com.efcon.tripservice.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TripService {

    private static final Map<TripStatus, Set<TripStatus>> ALLOWED_STATUS_TRANSITIONS = new EnumMap<>(TripStatus.class);

    static {
        ALLOWED_STATUS_TRANSITIONS.put(TripStatus.CREATED,
                EnumSet.of(TripStatus.ACCEPTED, TripStatus.CANCELED));
        ALLOWED_STATUS_TRANSITIONS.put(TripStatus.ACCEPTED,
                EnumSet.of(TripStatus.ON_THE_WAY_TO_PASSENGER, TripStatus.CANCELED));
        ALLOWED_STATUS_TRANSITIONS.put(TripStatus.ON_THE_WAY_TO_PASSENGER,
                EnumSet.of(TripStatus.ON_THE_WAY_TO_DESTINATION, TripStatus.CANCELED));
        ALLOWED_STATUS_TRANSITIONS.put(TripStatus.ON_THE_WAY_TO_DESTINATION,
                EnumSet.of(TripStatus.COMPLETED, TripStatus.CANCELED));
        ALLOWED_STATUS_TRANSITIONS.put(TripStatus.COMPLETED, EnumSet.noneOf(TripStatus.class));
        ALLOWED_STATUS_TRANSITIONS.put(TripStatus.CANCELED, EnumSet.noneOf(TripStatus.class));
    }

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final TripValidationService tripValidationService;
    private final SagaExecutor sagaExecutor;
    private final TripStatusPublicationService tripStatusPublicationService;


    public TripResponseDto create(TripRequestDto requestDto) {


        Trip trip = tripMapper.toEntity(requestDto);
        trip.setStatus(TripStatus.CREATED);
        trip.setSagaStatus(SagaStatus.IN_PROGRESS);
        trip.setOrderDateTime(LocalDateTime.now());
        try {
            sagaExecutor.execute("trip-create", List.of(
                    validateReferencesStep(requestDto),
                    persistTripStep(trip)
            ));
            trip.setSagaStatus(SagaStatus.COMPLETED);
            trip.setSagaFailureReason(null);
            return tripMapper.toDto(tripRepository.save(trip));
        } catch (SagaExecutionException ex) {
            trip.setSagaStatus(SagaStatus.FAILED);
            trip.setStatus(TripStatus.CANCELED);
            trip.setSagaFailureReason(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            return tripMapper.toDto(tripRepository.save(trip));
        }
    }

    public List<TripResponseDto> getAll() {
        return tripRepository.findAll().stream().map(tripMapper::toDto).toList();
    }

    public TripResponseDto getById(String id) {

        return tripMapper.toDto(findTrip(id));

    }

    public boolean existsById(String id) {
        return tripRepository.existsById(id);
    }


    public TripResponseDto update(String id, TripRequestDto requestDto) {



        Trip trip = findTrip(id);
        validateTripIsEditable(trip.getStatus());
        tripValidationService.validateReferences(requestDto.getPassengerId(), requestDto.getDriverId());
        trip.setDriverId(requestDto.getDriverId());
        trip.setPassengerId(requestDto.getPassengerId());
        trip.setPickupAddress(requestDto.getPickupAddress());
        trip.setDestinationAddress(requestDto.getDestinationAddress());
        trip.setCost(requestDto.getCost());
        return tripMapper.toDto(tripRepository.save(trip));

    }

    public void delete(String id) {

        tripRepository.deleteById(id);

    }

    public TripResponseDto updateStatus(String id, TripStatus status) {

        Trip trip = findTrip(id);
        validateStatusTransition(trip.getStatus(), status);
        if (trip.getStatus() != status) {
            trip.setStatus(status);
            trip.setPendingStatusEvent(PendingTripStatusEvent.forStatus(status));
            trip = tripRepository.save(trip);
        }

        if (hasPendingEventForStatus(trip, status)) {
            tripStatusPublicationService.publishPendingEventOrThrow(trip);
            trip = findTrip(id);
        }

        return tripMapper.toDto(trip);


    }
    private boolean hasPendingEventForStatus(Trip trip, TripStatus status) {
        return trip.getPendingStatusEvent() != null && trip.getPendingStatusEvent().getStatus() == status;


    }

    private Trip findTrip(String id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));
    }

    private void validateTripIsEditable(TripStatus status) {
        if (status == TripStatus.COMPLETED || status == TripStatus.CANCELED) {
            throw new IllegalStateException("Trip is not editable in status: " + status);
        }
    }


    private void validateStatusTransition(TripStatus current, TripStatus target) {
        if (current == target) {
            return;
        }
        Set<TripStatus> allowedTargets = ALLOWED_STATUS_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(TripStatus.class));
        if (!allowedTargets.contains(target)) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " -> " + target
            );
        }
    }


    private SagaStep validateReferencesStep(TripRequestDto requestDto) {
        return () -> tripValidationService.validateReferences(requestDto.getPassengerId(), requestDto.getDriverId());
    }

    private SagaStep persistTripStep(Trip trip) {
        return new SagaStep() {
            @Override
            public void execute() {
                tripRepository.save(trip);
            }

            @Override
            public void compensate() {
                if (trip.getId() != null) {
                    tripRepository.deleteById(trip.getId());
                }
            }
        };
    }

}

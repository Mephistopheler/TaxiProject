package com.efcon.tripservice.service;


import com.efcon.tripservice.dto.TripRequestDto;
import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.mapper.TripMapper;
import com.efcon.tripservice.model.SagaStatus;
import com.efcon.tripservice.model.Trip;
import com.efcon.tripservice.model.TripStatus;
import com.efcon.tripservice.repository.TripRepository;
import com.efcon.tripservice.saga.SagaExecutionException;
import com.efcon.tripservice.saga.SagaExecutor;
import com.efcon.tripservice.saga.SagaStep;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final TripValidationService tripValidationService;
    private final SagaExecutor sagaExecutor;


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


        tripValidationService.validateReferences(requestDto.getPassengerId(), requestDto.getDriverId());
        Trip trip = findTrip(id);
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
        trip.setStatus(status);
        return tripMapper.toDto(tripRepository.save(trip));


    }

    private Trip findTrip(String id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));
    }

    private void validateStatusTransition(TripStatus current, TripStatus target) {
        if (current == TripStatus.COMPLETED || current == TripStatus.CANCELED) {
            throw new IllegalStateException("Cannot change terminal status");
        }
        if (current == TripStatus.CREATED && target == TripStatus.ON_THE_WAY_TO_DESTINATION) {
            throw new IllegalStateException("Invalid status transition");
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

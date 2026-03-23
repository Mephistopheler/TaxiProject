package com.efcon.tripservice.model;

import com.efcon.tripservice.messaging.TripStatusChangedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingTripStatusEvent {
    private String eventId;
    private TripStatus status;
    private LocalDateTime changedAt;

    public static PendingTripStatusEvent forStatus(TripStatus status) {
        return PendingTripStatusEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .status(status)
                .changedAt(LocalDateTime.now())
                .build();
    }

    public TripStatusChangedEvent toEvent(Trip trip) {
        return new TripStatusChangedEvent(
                eventId,
                trip.getId(),
                trip.getPassengerId(),
                trip.getDriverId(),
                status,
                changedAt
        );
    }
}
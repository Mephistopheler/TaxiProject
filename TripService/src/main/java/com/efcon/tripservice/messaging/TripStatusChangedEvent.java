package com.efcon.tripservice.messaging;

import com.efcon.tripservice.model.TripStatus;

import java.time.LocalDateTime;

public record TripStatusChangedEvent(
        String tripId,
        Long passengerId,
        Long driverId,
        TripStatus status,
        LocalDateTime changedAt
) {
}
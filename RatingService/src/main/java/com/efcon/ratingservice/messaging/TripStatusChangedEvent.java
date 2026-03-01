package com.efcon.ratingservice.messaging;

import java.time.LocalDateTime;

public record TripStatusChangedEvent(
        String tripId,
        Long passengerId,
        Long driverId,
        String status,
        LocalDateTime changedAt
) {
}
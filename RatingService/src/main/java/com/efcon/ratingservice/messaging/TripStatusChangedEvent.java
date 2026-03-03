package com.efcon.ratingservice.messaging;

import java.time.LocalDateTime;

public record TripStatusChangedEvent(
        String eventId,
        String tripId,
        Long passengerId,
        Long driverId,
        String status,
        LocalDateTime changedAt
) {
}
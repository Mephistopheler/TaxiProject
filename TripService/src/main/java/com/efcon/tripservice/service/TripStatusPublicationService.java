package com.efcon.tripservice.service;

import com.efcon.tripservice.messaging.TripStatusChangedEvent;
import com.efcon.tripservice.messaging.TripStatusEventProducer;
import com.efcon.tripservice.model.PendingTripStatusEvent;
import com.efcon.tripservice.model.Trip;
import com.efcon.tripservice.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripStatusPublicationService {

    private final TripRepository tripRepository;
    private final TripStatusEventProducer tripStatusEventProducer;

    public void publishPendingEventOrThrow(Trip trip) {
        PendingTripStatusEvent pendingEvent = requirePendingEvent(trip);
        TripStatusChangedEvent event = pendingEvent.toEvent(trip);
        tripStatusEventProducer.publish(event);
        clearPendingEvent(trip.getId(), pendingEvent.getEventId());
    }

    @Scheduled(fixedDelayString = "${app.kafka.trip-status-retry-delay-ms:5000}")
    public void publishPendingEvents() {
        tripRepository.findAllByPendingStatusEventIsNotNull()
                .forEach(this::publishPendingEventSafely);
    }

    private void publishPendingEventSafely(Trip trip) {
        try {
            publishPendingEventOrThrow(trip);
        } catch (RuntimeException ex) {
            log.warn("Failed to publish pending trip status event for trip {}", trip.getId(), ex);
        }
    }

    private PendingTripStatusEvent requirePendingEvent(Trip trip) {
        PendingTripStatusEvent pendingEvent = trip.getPendingStatusEvent();
        if (pendingEvent == null) {
            throw new IllegalStateException("No pending status event for trip: " + trip.getId());
        }
        return pendingEvent;
    }

    private void clearPendingEvent(String tripId, String eventId) {
        Trip persistedTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));
        PendingTripStatusEvent pendingEvent = persistedTrip.getPendingStatusEvent();
        if (pendingEvent != null && eventId.equals(pendingEvent.getEventId())) {
            persistedTrip.setPendingStatusEvent(null);
            tripRepository.save(persistedTrip);
        }
    }
}
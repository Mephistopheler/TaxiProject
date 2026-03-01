package com.efcon.ratingservice.messaging;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TripCompletionRegistry {

    private final Set<String> completedTripIds = ConcurrentHashMap.newKeySet();

    public void markCompleted(String tripId) {
        completedTripIds.add(tripId);
    }

    public boolean isCompleted(String tripId) {
        return completedTripIds.contains(tripId);
    }
}
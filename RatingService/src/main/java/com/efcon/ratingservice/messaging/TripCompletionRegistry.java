package com.efcon.ratingservice.messaging;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TripCompletionRegistry {

    private static final String COMPLETED_TRIP_KEY_PREFIX = "trip:completed:";
    private static final String PROCESSED_EVENT_KEY_PREFIX = "trip:event:processed:";

    private final StringRedisTemplate redisTemplate;

    public TripCompletionRegistry(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void markCompleted(String tripId) {
        redisTemplate.opsForValue().set(COMPLETED_TRIP_KEY_PREFIX + tripId, "1");
    }

    public boolean isCompleted(String tripId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(COMPLETED_TRIP_KEY_PREFIX + tripId));
    }

    public boolean markEventProcessedIfFirst(String eventId) {
        Boolean firstTime = redisTemplate.opsForValue().setIfAbsent(PROCESSED_EVENT_KEY_PREFIX + eventId, "1");
        return Boolean.TRUE.equals(firstTime);
    }
}
package com.efcon.ratingservice.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripStatusEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TripStatusEventConsumer.class);

    private final TripCompletionRegistry tripCompletionRegistry;

    @KafkaListener(
            topics = "${app.kafka.trip-status-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "tripStatusKafkaListenerContainerFactory"
    )
    public void onTripStatusChanged(TripStatusChangedEvent event, Acknowledgment acknowledgment) {
        log.info("Received trip status event: {}", event);

        if ("COMPLETED".equals(event.status())) {
            tripCompletionRegistry.markCompleted(event.tripId());
        }

        acknowledgment.acknowledge();
    }
}
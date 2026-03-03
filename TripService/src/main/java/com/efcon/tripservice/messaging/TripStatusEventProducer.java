package com.efcon.tripservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripStatusEventProducer {

    private final KafkaTemplate<String, TripStatusChangedEvent> kafkaTemplate;

    @Value("${app.kafka.trip-status-topic}")
    private String tripStatusTopic;

    public void publish(TripStatusChangedEvent event) {
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(tripStatusTopic, event.tripId(), event).join();
            return null;
        });
    }
}
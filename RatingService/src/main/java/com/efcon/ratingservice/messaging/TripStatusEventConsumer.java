package com.efcon.ratingservice.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripStatusEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TripStatusEventConsumer.class);

    private final TripCompletionRegistry tripCompletionRegistry;

    @Value("${app.kafka.delivery-semantics:AT_LEAST_ONCE}")
    private DeliverySemantics deliverySemantics;


    @KafkaListener(
            topics = "${app.kafka.trip-status-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "tripStatusKafkaListenerContainerFactory"
    )
    public void onTripStatusChanged(TripStatusChangedEvent event, Acknowledgment acknowledgment) {
        log.info("Received trip status event: {}", event);

        if (deliverySemantics == DeliverySemantics.AT_MOST_ONCE) {
            acknowledgment.acknowledge();
        }

        if (deliverySemantics == DeliverySemantics.EFFECTIVELY_ONCE
                && !tripCompletionRegistry.markEventProcessedIfFirst(event.eventId())) {
            log.info("Skip duplicated event {}", event.eventId());
            acknowledgment.acknowledge();
            return;
        }


        if ("COMPLETED".equals(event.status())) {
            tripCompletionRegistry.markCompleted(event.tripId());
        }

        if (deliverySemantics != DeliverySemantics.AT_MOST_ONCE) {
            acknowledgment.acknowledge();
        }
    }
}
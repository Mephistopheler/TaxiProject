package com.efcon.ratingservice.rating.service.validation;


import com.efcon.ratingservice.client.PassengerClient;
import com.efcon.ratingservice.client.TripClient;
import com.efcon.ratingservice.config.DriverExistsRequest;
import com.efcon.ratingservice.config.DriverValidationServiceGrpc;
import com.efcon.ratingservice.messaging.TripCompletionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service

public class RatingValidationService {
    private final TripClient tripClient;
    private final PassengerClient passengerClient;
    private final DriverValidationServiceGrpc.DriverValidationServiceBlockingStub driverValidationStub;
    private final TripCompletionRegistry tripCompletionRegistry;


    public RatingValidationService(
            @Qualifier("com.efcon.ratingservice.client.TripClient") TripClient tripClient,
            @Qualifier("com.efcon.ratingservice.client.PassengerClient") PassengerClient passengerClient,
            DriverValidationServiceGrpc.DriverValidationServiceBlockingStub driverValidationStub,
            TripCompletionRegistry tripCompletionRegistry
    ) {
        this.tripClient = tripClient;
        this.passengerClient = passengerClient;
        this.driverValidationStub = driverValidationStub;
        this.tripCompletionRegistry = tripCompletionRegistry;
    }
    public void validateReferences(String tripId, Long passengerId, Long driverId) {
        if (!tripClient.existsById(tripId).isExists()) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }

        if (!tripCompletionRegistry.isCompleted(tripId)) {
            throw new IllegalStateException("Trip is not completed yet: " + tripId);
        }


        if (!passengerClient.existsById(passengerId).isExists()) {
            throw new IllegalArgumentException("Passenger not found: " + passengerId);
        }

        boolean driverExists = driverValidationStub.checkDriverExists(
                DriverExistsRequest.newBuilder().setDriverId(driverId).build()
        ).getExists();

        if (!driverExists) {
            throw new IllegalArgumentException("Driver not found: " + driverId);
        }
    }
}

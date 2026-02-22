package com.efcon.ratingservice.rating.service.validation;


import com.efcon.ratingservice.client.PassengerClient;
import com.efcon.ratingservice.client.TripClient;
import com.efcon.ratingservice.config.DriverExistsRequest;
import com.efcon.ratingservice.config.DriverValidationServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingValidationService {
    private final TripClient tripClient;
    private final PassengerClient passengerClient;
    private final DriverValidationServiceGrpc.DriverValidationServiceBlockingStub driverValidationStub;

    public void validateReferences(String tripId, Long passengerId, Long driverId) {
        if (!tripClient.existsById(tripId).isExists()) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
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

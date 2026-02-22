package com.efcon.tripservice.service;

import com.efcon.tripservice.client.PassengerClient;
import com.efcon.tripservice.grpc.DriverExistsRequest;
import com.efcon.tripservice.grpc.DriverValidationServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripValidationService {

    private final PassengerClient passengerClient;
    private final DriverValidationServiceGrpc.DriverValidationServiceBlockingStub driverValidationStub;

    public void validateReferences(Long passengerId, Long driverId) {
        boolean passengerExists = passengerClient.existsById(passengerId).isExists();
        if (!passengerExists) {
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
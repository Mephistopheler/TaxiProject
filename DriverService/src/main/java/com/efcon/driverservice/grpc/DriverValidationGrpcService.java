package com.efcon.driverservice.grpc;

import com.efcon.driverservice.driver.service.DriverService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class DriverValidationGrpcService extends DriverValidationServiceGrpc.DriverValidationServiceImplBase {

    private final DriverService driverService;

    @Override
    public void checkDriverExists(DriverExistsRequest request, StreamObserver<DriverExistsResponse> responseObserver) {
        boolean exists = driverService.existsById(request.getDriverId());

        responseObserver.onNext(DriverExistsResponse.newBuilder().setExists(exists).build());
        responseObserver.onCompleted();
    }
}
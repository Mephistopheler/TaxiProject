package com.efcon.tripservice.config;

import com.efcon.tripservice.grpc.DriverValidationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel driverValidationManagedChannel(@Value("${driver.grpc.address}") String driverGrpcAddress) {
        String[] hostPort = driverGrpcAddress.split(":");
        return ManagedChannelBuilder.forAddress(hostPort[0], Integer.parseInt(hostPort[1]))
                .usePlaintext()
                .build();
    }

    @Bean
    public DriverValidationServiceGrpc.DriverValidationServiceBlockingStub driverValidationServiceBlockingStub(
            ManagedChannel driverValidationManagedChannel
    ) {
        return DriverValidationServiceGrpc.newBlockingStub(driverValidationManagedChannel);
    }
}
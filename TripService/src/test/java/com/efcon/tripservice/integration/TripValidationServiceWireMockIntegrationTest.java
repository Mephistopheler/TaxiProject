package com.efcon.tripservice.integration;

import com.efcon.tripservice.grpc.DriverExistsResponse;
import com.efcon.tripservice.grpc.DriverValidationServiceGrpc;
import com.efcon.tripservice.service.TripValidationService;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = {com.efcon.tripservice.TripServiceApplication.class, TripValidationServiceWireMockIntegrationTest.TestConfig.class})
class TripValidationServiceWireMockIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance().options(options().dynamicPort()).build();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.PassengerService.url", wireMock::baseUrl);
        registry.add("eureka.client.enabled", () -> false);
        registry.add("spring.cloud.discovery.enabled", () -> false);
    }

    @Autowired TripValidationService tripValidationService;

    @Test
    void validateReferences_callsPassengerServiceViaFeign() {
        wireMock.stubFor(get(urlEqualTo("/api/passengers/5/exists"))
                .willReturn(okJson("{\"exists\":true}")));

        assertDoesNotThrow(() -> tripValidationService.validateReferences(5L, 8L));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        DriverValidationServiceGrpc.DriverValidationServiceBlockingStub driverValidationStub() {
            DriverValidationServiceGrpc.DriverValidationServiceBlockingStub stub = Mockito.mock(DriverValidationServiceGrpc.DriverValidationServiceBlockingStub.class);
            Mockito.when(stub.checkDriverExists(Mockito.any()))
                    .thenReturn(DriverExistsResponse.newBuilder().setExists(true).build());
            return stub;
        }
    }
}
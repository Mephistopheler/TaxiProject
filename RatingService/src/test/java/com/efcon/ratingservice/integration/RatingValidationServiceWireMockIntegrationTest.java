package com.efcon.ratingservice.integration;

import com.efcon.ratingservice.config.DriverExistsResponse;
import com.efcon.ratingservice.config.DriverValidationServiceGrpc;
import com.efcon.ratingservice.messaging.TripCompletionRegistry;
import com.efcon.ratingservice.rating.service.validation.RatingValidationService;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
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

@SpringBootTest(classes = {com.efcon.ratingservice.RatingServiceApplication.class, RatingValidationServiceWireMockIntegrationTest.TestConfig.class})
class RatingValidationServiceWireMockIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance().options(WireMockConfiguration.wireMockConfig().dynamicPort()).build();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.TripService.url", wireMock::baseUrl);
        registry.add("spring.cloud.openfeign.client.config.PassengerService.url", wireMock::baseUrl);
        registry.add("eureka.client.enabled", () -> false);
        registry.add("spring.cloud.discovery.enabled", () -> false);
    }

    @Autowired RatingValidationService ratingValidationService;

    @Test
    void validateReferences_callsTripAndPassengerViaFeign() {
        wireMock.stubFor(get(urlEqualTo("/api/trips/t1/exists")).willReturn(okJson("{\"exists\":true}")));
        wireMock.stubFor(get(urlEqualTo("/api/passengers/2/exists")).willReturn(okJson("{\"exists\":true}")));

        assertDoesNotThrow(() -> ratingValidationService.validateReferences("t1", 2L, 3L));
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

        @Bean
        TripCompletionRegistry tripCompletionRegistry() {
            TripCompletionRegistry registry = Mockito.mock(TripCompletionRegistry.class);
            Mockito.when(registry.isCompleted("t1")).thenReturn(true);
            return registry;
        }
    }
}
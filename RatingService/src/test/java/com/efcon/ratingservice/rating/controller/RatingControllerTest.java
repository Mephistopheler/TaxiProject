package com.efcon.ratingservice.rating.controller;

import com.efcon.ratingservice.rating.dto.RatingResponseDto;
import com.efcon.ratingservice.rating.model.RaterType;
import com.efcon.ratingservice.rating.service.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
class RatingControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean RatingService ratingService;

    @Test
    void rateTrip_returnsCreated() throws Exception {
        when(ratingService.rateTrip(any())).thenReturn(new RatingResponseDto());
        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tripId\":\"t1\",\"driverId\":1,\"passengerId\":2,\"score\":5,\"raterType\":\"PASSENGER\"}"))
                .andExpect(status().isCreated());
    }
    @Test
    void rateTrip_returnsBadRequestWhenValidationFails() throws Exception {
        when(ratingService.rateTrip(any())).thenThrow(new IllegalArgumentException("Trip not found"));

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tripId\":\"t1\",\"driverId\":1,\"passengerId\":2,\"score\":5,\"raterType\":\"PASSENGER\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Trip not found"));
    }

    @Test
    void rateTrip_returnsConflictWhenTripStateIsInvalid() throws Exception {
        when(ratingService.rateTrip(any())).thenThrow(new IllegalStateException("Trip is not completed yet"));

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tripId\":\"t1\",\"driverId\":1,\"passengerId\":2,\"score\":5,\"raterType\":\"PASSENGER\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Trip is not completed yet"));
    }
}
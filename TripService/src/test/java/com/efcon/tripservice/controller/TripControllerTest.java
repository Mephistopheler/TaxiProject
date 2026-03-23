package com.efcon.tripservice.controller;

import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.model.TripStatus;
import com.efcon.tripservice.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
class TripControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean TripService tripService;

    @Test
    void getById_returnsOk() throws Exception {
        when(tripService.getById("id1")).thenReturn(new TripResponseDto());
        mockMvc.perform(get("/api/trips/id1"))
                .andExpect(status().isOk());
    }
    @Test
    void getById_returnsBadRequestWhenTripMissing() throws Exception {
        when(tripService.getById("missing")).thenThrow(new IllegalArgumentException("Trip not found: missing"));

        mockMvc.perform(get("/api/trips/missing"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Trip not found: missing"));
    }

    @Test
    void updateStatus_returnsConflictWhenTransitionInvalid() throws Exception {
        when(tripService.updateStatus("id1", TripStatus.ACCEPTED))
                .thenThrow(new IllegalStateException("Invalid status transition"));

        mockMvc.perform(patch("/api/trips/id1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACCEPTED\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Invalid status transition"));
    }
}
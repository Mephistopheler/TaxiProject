package com.efcon.tripservice.controller;

import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
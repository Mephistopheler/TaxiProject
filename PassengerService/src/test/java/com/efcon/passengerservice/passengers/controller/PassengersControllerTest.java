package com.efcon.passengerservice.passengers.controller;

import com.efcon.passengerservice.passengers.model.Passengers;
import com.efcon.passengerservice.passengers.service.PassengersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengersController.class)
class PassengersControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean PassengersService passengersService;

    @Test
    void existsById_returnsFlag() throws Exception {
        when(passengersService.existsPassengerById(5L)).thenReturn(true);
        mockMvc.perform(get("/api/passengers/5/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void create_returnsCreated() throws Exception {
        Passengers p = new Passengers();
        p.setId(1L);
        when(passengersService.savePassenger(any())).thenReturn(p);
        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"email\":\"a@a.com\",\"phone\":\"1\"}"))
                .andExpect(status().isCreated());
    }
    @Test
    void create_returnsBadRequestWhenServiceThrowsIllegalArgument() throws Exception {
        when(passengersService.savePassenger(any())).thenThrow(new IllegalArgumentException("invalid passenger"));

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"email\":\"a@a.com\",\"phone\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid passenger"));
    }
}
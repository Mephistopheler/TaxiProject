package com.efcon.driverservice.driver.controller;

import com.efcon.driverservice.driver.controller.DriverController;
import com.efcon.driverservice.driver.model.Driver;
import com.efcon.driverservice.driver.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private DriverService driverService;

    @Test
    void findById_returnsOk() throws Exception {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setName("Ivan");
        when(driverService.findDriverById(1L)).thenReturn(Optional.of(driver));

        mockMvc.perform(get("/api/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_returnsBadRequestWhenServiceThrows() throws Exception {
        when(driverService.createDriver(any(), any())).thenThrow(new IllegalArgumentException("bad"));

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ivan\",\"carPlateNumber\":\"bad\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("bad"));
    }

    @Test
    void update_returnsConflictWhenServiceThrowsIllegalState() throws Exception {
        when(driverService.updateDriver(eq(1L), any(), any())).thenThrow(new IllegalStateException("conflict"));

        mockMvc.perform(put("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ivan\",\"carPlateNumber\":\"1234 AB-7\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("conflict"));
    }
}
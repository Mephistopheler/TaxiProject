package com.efcon.passengerservice.e2e;

import com.efcon.passengerservice.passengers.model.Passengers;
import com.efcon.passengerservice.passengers.service.PassengersService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PassengerE2ESteps {
    @Autowired MockMvc mockMvc;
    @Autowired PassengersService passengersService;
    private ResultActions result;

    @Given("passenger with id {long} exists")
    public void passengerWithIdExists(long id) {
        when(passengersService.existsPassengerById(id)).thenReturn(true);
        Passengers p = new Passengers(); p.setId(id);
        when(passengersService.findPassengerById(id)).thenReturn(Optional.of(p));
    }

    @When("e2e client checks passenger existence for id {long}")
    public void e2eClientChecksPassengerExistenceForId(long id) throws Exception {
        result = mockMvc.perform(get("/api/passengers/{id}/exists", id));
    }

    @Then("passenger e2e status is {int}")
    public void passengerE2eStatusIs(int code) throws Exception {
        result.andExpect(status().is(code));
    }
}
package com.efcon.tripservice.e2e;

import com.efcon.tripservice.service.TripService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TripE2ESteps {
    @Autowired MockMvc mockMvc;
    @Autowired TripService tripService;
    ResultActions result;

    @Given("trip service returns true for id {string}")
    public void tripServiceReturnsTrueForId(String id) {
        when(tripService.existsById(id)).thenReturn(true);
    }

    @When("e2e client checks trip existence for {string}")
    public void e2eClientChecksTripExistenceFor(String id) throws Exception {
        result = mockMvc.perform(get("/api/trips/{id}/exists", id));
    }

    @Then("trip e2e status is {int}")
    public void tripE2eStatusIs(int code) throws Exception {
        result.andExpect(status().is(code));
    }
}
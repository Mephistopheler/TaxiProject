package com.efcon.driverservice.e2e;

import com.efcon.driverservice.driver.model.Driver;
import com.efcon.driverservice.driver.service.DriverService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DriverE2ESteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverService driverService;

    private ResultActions result;

    @Given("driver service has driver with id {long}")
    public void driverServiceHasDriverWithId(long id) {
        Driver d = new Driver();
        d.setId(id);
        d.setName("Test");
        when(driverService.findDriverById(id)).thenReturn(Optional.of(d));
    }

    @When("e2e client requests driver with id {long}")
    public void e2eClientRequestsDriverWithId(long id) throws Exception {
        result = mockMvc.perform(get("/api/drivers/{id}", id).accept(MediaType.APPLICATION_JSON));
    }

    @Then("e2e response status is {int}")
    public void e2eResponseStatusIs(int statusCode) throws Exception {
        result.andExpect(status().is(statusCode));
    }
}
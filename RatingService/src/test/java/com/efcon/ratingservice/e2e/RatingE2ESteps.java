package com.efcon.ratingservice.e2e;

import com.efcon.ratingservice.rating.dto.RatingResponseDto;
import com.efcon.ratingservice.rating.service.RatingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RatingE2ESteps {
    @Autowired MockMvc mockMvc;
    @Autowired RatingService ratingService;
    ResultActions result;

    @Given("rating service is ready to accept rates")
    public void ratingServiceIsReadyToAcceptRates() {
        when(ratingService.rateTrip(any())).thenReturn(new RatingResponseDto());
    }

    @When("e2e client sends a valid rating")
    public void e2eClientSendsAValidRating() throws Exception {
        result = mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tripId\":\"t1\",\"driverId\":1,\"passengerId\":2,\"score\":5,\"raterType\":\"PASSENGER\"}"));
    }

    @Then("rating e2e status is {int}")
    public void ratingE2eStatusIs(int code) throws Exception {
        result.andExpect(status().is(code));
    }
}
package com.efcon.ratingservice.rating.controller;

import com.efcon.ratingservice.rating.dto.RatingRequestDto;
import com.efcon.ratingservice.rating.dto.RatingResponseDto;
import com.efcon.ratingservice.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);

    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponseDto rateTrip(@Valid @RequestBody RatingRequestDto requestDto) {
        log.info("Received rating request: {}", requestDto);

        // Обрабатываем запрос
        RatingResponseDto response = ratingService.rateTrip(requestDto);
        log.info("Rating successfully created: {}", response);

        return response;
    }

    @GetMapping("/{tripId}")
    public List<RatingResponseDto> getTripRatings(@PathVariable String tripId) {
        log.info("Received request to fetch ratings for tripId: {}", tripId);

        // Получаем рейтинги
        List<RatingResponseDto> ratings = ratingService.getByTrip(tripId);
        if (ratings.isEmpty()) {
            log.warn("No ratings found for tripId: {}", tripId);
        } else {
            log.info("Found {} ratings for tripId: {}", ratings.size(), tripId);
        }

        return ratings;
    }
}

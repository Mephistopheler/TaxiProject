package com.efcon.ratingservice.rating.service;

import com.efcon.ratingservice.rating.dto.RatingRequestDto;
import com.efcon.ratingservice.rating.dto.RatingResponseDto;
import com.efcon.ratingservice.rating.mapper.RatingMapper;
import com.efcon.ratingservice.rating.model.Rating;
import com.efcon.ratingservice.rating.repository.RatingRepository;
import com.efcon.ratingservice.rating.service.validation.RatingValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    @Mock RatingRepository ratingRepository;
    @Mock RatingMapper ratingMapper;
    @Mock RatingValidationService ratingValidationService;
    @InjectMocks RatingService ratingService;

    @Test
    void rateTrip_validatesAndSaves() {
        RatingRequestDto req = new RatingRequestDto();
        req.setTripId("t1"); req.setPassengerId(1L); req.setDriverId(2L);
        Rating rating = new Rating();
        RatingResponseDto response = new RatingResponseDto();
        when(ratingMapper.toEntity(req)).thenReturn(rating);
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toDto(rating)).thenReturn(response);

        RatingResponseDto result = ratingService.rateTrip(req);

        assertEquals(response, result);
        verify(ratingValidationService).validateReferences("t1", 1L, 2L);
    }
}
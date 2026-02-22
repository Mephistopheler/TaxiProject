package com.efcon.ratingservice.rating.service;

import com.efcon.ratingservice.rating.dto.RatingRequestDto;
import com.efcon.ratingservice.rating.dto.RatingResponseDto;
import com.efcon.ratingservice.rating.mapper.RatingMapper;
import com.efcon.ratingservice.rating.model.Rating;
import com.efcon.ratingservice.rating.repository.RatingRepository;
import com.efcon.ratingservice.rating.service.validation.RatingValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {



    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final RatingValidationService ratingValidationService;

    public RatingResponseDto rateTrip(RatingRequestDto requestDto) {


        ratingValidationService.validateReferences(
                requestDto.getTripId(),
                requestDto.getPassengerId(),
                requestDto.getDriverId()
        );

        // Маппим DTO в сущность
        Rating rating = ratingMapper.toEntity(requestDto);


        // Сохраняем в Redis
        Rating savedRating = ratingRepository.save(rating);


        // Маппим обратно в DTO и возвращаем
        RatingResponseDto responseDto = ratingMapper.toDto(savedRating);


        return responseDto;
    }

    public List<RatingResponseDto> getByTrip(String tripId) {


        List<Rating> ratings = ratingRepository.findByTripId(tripId);

        List<RatingResponseDto> responseDtos = ratings.stream()
                .map(ratingMapper::toDto)
                .toList();



        return responseDtos;
    }
}

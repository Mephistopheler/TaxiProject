package com.efcon.ratingservice.rating.repository;

import com.efcon.ratingservice.rating.model.Rating;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;


@Repository
@EnableRedisRepositories
public interface RatingRepository extends CrudRepository<Rating, String> {
    List<Rating> findAllByTripId(String tripId);
}

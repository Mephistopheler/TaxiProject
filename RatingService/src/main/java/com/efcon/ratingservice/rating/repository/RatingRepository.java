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
    default List<Rating> findByTripId(String tripId) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(rating -> Objects.equals(rating.getTripId(), tripId))
                .toList();
    }
}

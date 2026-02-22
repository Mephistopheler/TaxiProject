package com.efcon.ratingservice.client;

import com.efcon.ratingservice.client.dto.ExistenceResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "TripService")
public interface TripClient {

    @GetMapping("/api/trips/{id}/exists")
    ExistenceResponseDto existsById(@PathVariable("id") String id);
}
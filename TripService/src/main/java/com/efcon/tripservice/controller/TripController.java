package com.efcon.tripservice.controller;


import com.efcon.tripservice.client.dto.ExistenceResponseDto;
import com.efcon.tripservice.dto.TripRequestDto;
import com.efcon.tripservice.dto.TripResponseDto;
import com.efcon.tripservice.dto.TripStatusUpdateRequest;
import com.efcon.tripservice.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponseDto create(@Valid @RequestBody TripRequestDto requestDto){

        return tripService.create(requestDto);

    }

    @GetMapping
    public List<TripResponseDto> getAll(){

        return tripService.getAll();

    }

    @GetMapping("/{id}")
    public TripResponseDto getById(@PathVariable String id){

        return tripService.getById(id);

    }

    @GetMapping("/{id}/exists")
    public ExistenceResponseDto existsById(@PathVariable String id) {

        return new ExistenceResponseDto(tripService.existsById(id));

    }


    @PutMapping("/{id}")
    public TripResponseDto update(@PathVariable String id, @RequestBody TripRequestDto requestDto ){

        return tripService.update(id, requestDto);

    }

    @PatchMapping("/{id}/status")
    public TripResponseDto updateStatus(@PathVariable String id, @Valid @RequestBody TripStatusUpdateRequest request){

        return tripService.updateStatus(id, request.getStatus());

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id){

        tripService.delete(id);

    }



}

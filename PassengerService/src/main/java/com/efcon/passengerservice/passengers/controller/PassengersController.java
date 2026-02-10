package com.efcon.passengerservice.passengers.controller;


import com.efcon.passengerservice.passengers.dto.PassengerDto;
import com.efcon.passengerservice.passengers.model.Passengers;
import com.efcon.passengerservice.passengers.service.PassengersService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.stream.Collectors;


@RestController()
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengersController {

    private final PassengersService passengersService;




    @PostMapping
    public ResponseEntity<PassengerDto> createPassenger(@RequestBody PassengerDto passengerDto){

        Passengers savedPassenger = passengersService.savePassenger(toEntity(passengerDto));
        return new ResponseEntity<>(toDto(savedPassenger), HttpStatus.CREATED);


    }

    @GetMapping
    public ResponseEntity<List<PassengerDto>> findAllPassengers() {
        List<PassengerDto> passengers = passengersService.findAllPassengers()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDto> findPassengerById(@PathVariable long id) {
        return passengersService.findPassengerById(id)
                .map(passenger -> ResponseEntity.ok(toDto(passenger)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerDto> updatePassenger(
            @PathVariable long id,
            @RequestBody PassengerDto passengerDto
    ) {
        return passengersService.updatePassenger(id, toEntity(passengerDto))
                .map(passenger -> ResponseEntity.ok(toDto(passenger)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable long id) {
        boolean deleted = passengersService.softDeletePassenger(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    private Passengers toEntity(PassengerDto passengerDto) {
        Passengers passenger = new Passengers();
        passenger.setId(passengerDto.getId());
        passenger.setName(passengerDto.getName());
        passenger.setEmail(passengerDto.getEmail());
        passenger.setPhone(passengerDto.getPhone());
        return passenger;
    }

    private PassengerDto toDto(Passengers passenger) {
        PassengerDto passengerDto = new PassengerDto();
        passengerDto.setId(passenger.getId());
        passengerDto.setName(passenger.getName());
        passengerDto.setEmail(passenger.getEmail());
        passengerDto.setPhone(passenger.getPhone());
        return passengerDto;
    }

}

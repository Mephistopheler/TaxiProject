package com.efcon.driverservice.driver.controller;

import com.efcon.driverservice.driver.dto.CarDto;
import com.efcon.driverservice.driver.model.Car;
import com.efcon.driverservice.driver.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto carDto) {
        try {
            Car savedCar = carService.createCar(toEntity(carDto));
            return new ResponseEntity<>(toDto(savedCar), HttpStatus.CREATED);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> findAllCars() {
        List<CarDto> cars = carService.findAllCars()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{plateNumber}")
    public ResponseEntity<CarDto> findCarByPlateNumber(@PathVariable String plateNumber) {
        return carService.findCarByPlateNumber(plateNumber)
                .map(car -> ResponseEntity.ok(toDto(car)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{plateNumber}")
    public ResponseEntity<CarDto> updateCar(@PathVariable String plateNumber, @RequestBody CarDto carDto) {
        try {
            return carService.updateCar(plateNumber, toEntity(carDto))
                    .map(car -> ResponseEntity.ok(toDto(car)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{plateNumber}")
    public ResponseEntity<Void> deleteCar(@PathVariable String plateNumber) {
        boolean deleted = carService.softDeleteCar(plateNumber);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    private Car toEntity(CarDto carDto) {
        Car car = new Car();
        car.setPlateNumber(carDto.getPlateNumber());
        car.setColor(carDto.getColor());
        car.setBrand(carDto.getBrand());
        return car;
    }

    private CarDto toDto(Car car) {
        CarDto carDto = new CarDto();
        carDto.setPlateNumber(car.getPlateNumber());
        carDto.setColor(car.getColor());
        carDto.setBrand(car.getBrand());
        return carDto;
    }

}

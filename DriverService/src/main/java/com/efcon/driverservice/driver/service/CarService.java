package com.efcon.driverservice.driver.service;


import com.efcon.driverservice.driver.model.Car;
import com.efcon.driverservice.driver.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final Pattern BELARUS_PLATE_PATTERN = Pattern.compile("^[0-9]{4} [A-Z]{2}-[1-7]$");

    private final CarRepository carRepository;

    public Car createCar(Car car){
        validatePlateNumber(car.getPlateNumber());
        car.setDeleted(false);
        return carRepository.save(car);
    }

    public List<Car> findAllCars(){
        return carRepository.findAllByDeletedFalse();
    }

    public Optional<Car> findCarByPlateNumber(String plateNumber){
        return carRepository.findByPlateNumberAndDeletedFalse(plateNumber);
    }

    public Optional<Car> updateCar(String plateNumber, Car updatedCar) {
        validatePlateNumber(plateNumber);
        return carRepository.findByPlateNumberAndDeletedFalse(plateNumber)
                .map(existingCar -> {
                    existingCar.setColor(updatedCar.getColor());
                    existingCar.setBrand(updatedCar.getBrand());
                    return carRepository.save(existingCar);
                });
    }

    public boolean softDeleteCar(String plateNumber) {
        return carRepository.findByPlateNumberAndDeletedFalse(plateNumber)
                .map(car -> {
                    car.setDeleted(true);
                    carRepository.save(car);
                    return true;
                })
                .orElse(false);
    }

    private void validatePlateNumber(String plateNumber) {
        if (plateNumber == null || !BELARUS_PLATE_PATTERN.matcher(plateNumber).matches()) {
            throw new IllegalArgumentException("Plate number must match Belarus format: 1234 AB-7");
        }
    }
}

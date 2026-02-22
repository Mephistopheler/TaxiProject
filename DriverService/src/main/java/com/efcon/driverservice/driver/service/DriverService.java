package com.efcon.driverservice.driver.service;


import com.efcon.driverservice.driver.model.Car;
import com.efcon.driverservice.driver.model.Driver;
import com.efcon.driverservice.driver.repository.CarRepository;
import com.efcon.driverservice.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;

    public Driver createDriver(Driver driver, String carPlateNumber){
        driver.setDeleted(false);
        driver.setCar(resolveCar(carPlateNumber, null));
        return driverRepository.save(driver);
    }

    public List<Driver> findAllDrivers() {
        return driverRepository.findAllByDeletedFalse();
    }

    public Optional<Driver> findDriverById(Long id) {
        return driverRepository.findByIdAndDeletedFalse(id);
    }

    public Optional<Driver> updateDriver(Long id, Driver updatedDriver, String carPlateNumber) {
        return driverRepository.findByIdAndDeletedFalse(id)
                .map(existingDriver -> {
                    existingDriver.setName(updatedDriver.getName());
                    existingDriver.setEmail(updatedDriver.getEmail());
                    existingDriver.setPhone(updatedDriver.getPhone());
                    existingDriver.setCar(resolveCar(carPlateNumber, id));
                    return driverRepository.save(existingDriver);
                });
    }

    public boolean softDeleteDriver(Long id) {
        return driverRepository.findByIdAndDeletedFalse(id)
                .map(driver -> {
                    driver.setDeleted(true);
                    driverRepository.save(driver);
                    return true;
                })
                .orElse(false);
    }

    private Car resolveCar(String carPlateNumber, Long currentDriverId) {
        if (carPlateNumber == null || carPlateNumber.isBlank()) {
            return null;
        }

        Car car = carRepository.findByPlateNumberAndDeletedFalse(carPlateNumber)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carPlateNumber));

        driverRepository.findByCarPlateNumberAndDeletedFalse(carPlateNumber).ifPresent(driver -> {
            if (currentDriverId == null || !driver.getId().equals(currentDriverId)) {
                throw new IllegalArgumentException("Car is already assigned to driver id=" + driver.getId());
            }
        });

        return car;
    }

    public boolean existsById(Long id) {
        return driverRepository.findByIdAndDeletedFalse(id).isPresent();
    }


}

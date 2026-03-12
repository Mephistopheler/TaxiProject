package com.efcon.driverservice.driver.service;

import com.efcon.driverservice.driver.model.Car;
import com.efcon.driverservice.driver.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Test
    void createCar_setsDeletedFalseAndSaves() {
        Car car = new Car();
        car.setPlateNumber("1234 AB-7");
        when(carRepository.save(car)).thenReturn(car);

        Car result = carService.createCar(car);

        assertFalse(result.isDeleted());
        verify(carRepository).save(car);
    }

    @Test
    void updateCar_returnsEmptyWhenNotFound() {
        when(carRepository.findByPlateNumberAndDeletedFalse("1234 AB-7")).thenReturn(Optional.empty());

        Optional<Car> result = carService.updateCar("1234 AB-7", new Car());

        assertTrue(result.isEmpty());
    }

    @Test
    void createCar_throwsWhenPlateInvalid() {
        Car car = new Car();
        car.setPlateNumber("INVALID");

        assertThrows(IllegalArgumentException.class, () -> carService.createCar(car));
    }
}
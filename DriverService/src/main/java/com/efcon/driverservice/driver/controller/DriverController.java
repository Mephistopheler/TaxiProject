package com.efcon.driverservice.driver.controller;

import com.efcon.driverservice.driver.dto.DriverDto;
import com.efcon.driverservice.driver.model.Driver;
import com.efcon.driverservice.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverDto> createDriver(@RequestBody DriverDto driverDto) {
        try {
            Driver savedDriver = driverService.createDriver(toEntity(driverDto), driverDto.getCarPlateNumber());
            return new ResponseEntity<>(toDto(savedDriver), HttpStatus.CREATED);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DriverDto>> findAllDrivers() {
        List<DriverDto> drivers = driverService.findAllDrivers()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> findDriverById(@PathVariable Long id) {
        return driverService.findDriverById(id)
                .map(driver -> ResponseEntity.ok(toDto(driver)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> updateDriver(@PathVariable Long id, @RequestBody DriverDto driverDto) {
        try {
            return driverService.updateDriver(id, toEntity(driverDto), driverDto.getCarPlateNumber())
                    .map(driver -> ResponseEntity.ok(toDto(driver)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        boolean deleted = driverService.softDeleteDriver(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private Driver toEntity(DriverDto driverDto) {
        Driver driver = new Driver();
        driver.setId(driverDto.getId());
        driver.setName(driverDto.getName());
        driver.setEmail(driverDto.getEmail());
        driver.setPhone(driverDto.getPhone());
        return driver;
    }

    private DriverDto toDto(Driver driver) {
        DriverDto driverDto = new DriverDto();
        driverDto.setId(driver.getId());
        driverDto.setName(driver.getName());
        driverDto.setEmail(driver.getEmail());
        driverDto.setPhone(driver.getPhone());
        driverDto.setCarPlateNumber(driver.getCar() == null ? null : driver.getCar().getPlateNumber());
        return driverDto;
    }

}

package com.efcon.driverservice.driver.repository;

import com.efcon.driverservice.driver.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car,String> {

    Optional<Car> findByPlateNumberAndDeletedFalse(String plateNumber);

    List<Car> findAllByDeletedFalse();

}

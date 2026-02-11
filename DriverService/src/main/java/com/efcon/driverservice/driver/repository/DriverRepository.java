package com.efcon.driverservice.driver.repository;

import com.efcon.driverservice.driver.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByIdAndDeletedFalse(Long id);
    List<Driver> findAllByDeletedFalse();
    Optional<Driver> findByCarPlateNumberAndDeletedFalse(String carPlateNumber);


}

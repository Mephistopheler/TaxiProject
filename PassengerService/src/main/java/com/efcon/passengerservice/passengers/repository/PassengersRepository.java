package com.efcon.passengerservice.passengers.repository;

import com.efcon.passengerservice.passengers.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengersRepository extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findByEmailAndDeletedFalse(String email);

    Optional<Passenger> findByIdAndDeletedFalse(Long id);

    List<Passenger> findAllByDeletedFalse();

}

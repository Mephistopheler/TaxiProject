package com.efcon.passengerservice.passengers.service;

import com.efcon.passengerservice.passengers.model.Passengers;
import com.efcon.passengerservice.passengers.repository.PassengersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengersService {

    private final PassengersRepository passengersRepository;




    public Passengers savePassenger(Passengers passengers){

        passengers.setDeleted(false);
        return passengersRepository.save(passengers);

    };

    public List<Passengers> findAllPassengers(){

        return passengersRepository.findAllByDeletedFalse();

    };

    public Optional<Passengers> findPassengerById(long id) {


        return passengersRepository.findByIdAndDeletedFalse(id);
    }

    public Optional<Passengers> updatePassenger(long id, Passengers updatedPassenger) {
        return passengersRepository.findByIdAndDeletedFalse(id)
                .map(existingPassenger -> {
                    existingPassenger.setName(updatedPassenger.getName());
                    existingPassenger.setEmail(updatedPassenger.getEmail());
                    existingPassenger.setPhone(updatedPassenger.getPhone());
                    return passengersRepository.save(existingPassenger);
                });
    }

    public boolean softDeletePassenger(long id) {
        return passengersRepository.findByIdAndDeletedFalse(id)
                .map(passenger -> {
                    passenger.setDeleted(true);
                    passengersRepository.save(passenger);
                    return true;
                })
                .orElse(false);


    }

}

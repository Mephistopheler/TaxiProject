package com.efcon.passengerservice.passengers.service;

import com.efcon.passengerservice.passengers.model.Passenger;
import com.efcon.passengerservice.passengers.repository.PassengersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengersService {

    private final PassengersRepository passengersRepository;




    public Passenger savePassenger(Passenger passenger){

        passenger.setDeleted(false);
        return passengersRepository.save(passenger);

    };

    public List<Passenger> findAllPassengers(){

        return passengersRepository.findAllByDeletedFalse();

    };

    public Optional<Passenger> findPassengerById(long id) {


        return passengersRepository.findByIdAndDeletedFalse(id);
    }

    public boolean existsPassengerById(long id){
        return passengersRepository.findByIdAndDeletedFalse(id).isPresent();
    }


    public Optional<Passenger> updatePassenger(long id, Passenger updatedPassenger) {
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

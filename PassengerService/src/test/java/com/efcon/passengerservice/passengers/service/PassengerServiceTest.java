package com.efcon.passengerservice.passengers.service;

import com.efcon.passengerservice.passengers.model.Passenger;
import com.efcon.passengerservice.passengers.repository.PassengersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {
    @Mock PassengersRepository passengersRepository;
    @InjectMocks PassengersService passengersService;

    @Test
    void savePassenger_marksNotDeleted() {
        Passenger p = new Passenger();
        when(passengersRepository.save(p)).thenReturn(p);
        Passenger saved = passengersService.savePassenger(p);
        assertFalse(saved.isDeleted());
    }

    @Test
    void updatePassenger_returnsEmptyWhenNotFound() {
        when(passengersRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertTrue(passengersService.updatePassenger(1L, new Passenger()).isEmpty());
    }
}
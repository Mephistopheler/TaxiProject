package com.efcon.passengerservice.passengers.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "name", "email", "phone"})
public class PassengerDto {

    private Long id;
    private String name;
    private String email;
    private String phone;

}

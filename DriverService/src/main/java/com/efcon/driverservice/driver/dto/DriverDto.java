package com.efcon.driverservice.driver.dto;

import lombok.Data;

@Data
public class DriverDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String carPlateNumber;
}

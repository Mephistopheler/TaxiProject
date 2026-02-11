package com.efcon.driverservice.driver.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @Column(name = "plate_number", nullable = false, length = 20)
    private String plateNumber;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
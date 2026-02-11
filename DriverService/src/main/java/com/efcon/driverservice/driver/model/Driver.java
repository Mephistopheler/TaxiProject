package com.efcon.driverservice.driver.model;

import com.efcon.driverservice.driver.model.Car;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drivers")
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_plate_number", referencedColumnName = "plate_number")
    private Car car;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
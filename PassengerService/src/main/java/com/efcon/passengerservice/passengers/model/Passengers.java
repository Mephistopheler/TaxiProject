package com.efcon.passengerservice.passengers.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "passengers")
@Data
public class Passengers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;



    public Passengers() {
    }

    public Passengers(String name, String email) {
        this.name = name;
        this.email=email;
    }

    public Passengers(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }



}
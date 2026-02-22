package com.efcon.tripservice.client.dto;

import lombok.Data;

@Data
public class ExistenceResponseDto {
    private boolean exists;

    public ExistenceResponseDto(boolean exists) {
        this.exists = exists;
    }
}
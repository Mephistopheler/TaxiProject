package com.efcon.tripservice.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExistenceResponseDto {
    private boolean exists;

    public ExistenceResponseDto(boolean exists) {
        this.exists = exists;
    }
}
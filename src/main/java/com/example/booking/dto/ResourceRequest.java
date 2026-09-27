package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResourceRequest {
    @NotBlank(message = "Resource name cannot be blank")
    private String name;
    private String description;
}
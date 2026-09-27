package com.example.booking.dto;

import com.example.booking.entity.ReservationStatus;
import com.example.booking.validation.ValidReservationTime;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ValidReservationTime
public class CreateReservationRequest {

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    private ReservationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
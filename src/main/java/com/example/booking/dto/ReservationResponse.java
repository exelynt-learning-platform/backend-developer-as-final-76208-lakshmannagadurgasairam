package com.example.booking.dto;

import com.example.booking.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long resourceId;
    private String resourceName;
    private BigDecimal price;
    private ReservationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
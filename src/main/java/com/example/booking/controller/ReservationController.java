package com.example.booking.controller;

import com.example.booking.dto.CreateReservationRequest;
import com.example.booking.dto.ReservationResponse;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.User;
import com.example.booking.exception.BadRequestException;
import com.example.booking.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
        summary = "Create a reservation",
        description = "Creates a reservation for the authenticated user. USER and ADMIN can create reservations."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid reservation data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request, currentUser));
    }

    @Operation(
        summary = "Get reservations",
        description = "Returns reservations for the authenticated user. ADMIN can view all reservations, while USER can view only their own reservations. Supports status, price filtering, pagination, and sorting."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination, sorting, or filter parameters"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getReservations(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        validatePageable(pageable);

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException(
                    "Minimum price cannot be greater than maximum price."
            );
        }

        return ResponseEntity.ok(
                reservationService.getReservations(
                        currentUser,
                        status,
                        minPrice,
                        maxPrice,
                        pageable
                )
        );
    }

    @Operation(
        summary = "Update reservation status",
        description = "Updates the status of a reservation. ADMIN only."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservation status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid reservation status"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN privileges required"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                reservationService.updateReservationStatus(
                        id,
                        status,
                        currentUser
                )
        );
    }

    @Operation(
        summary = "Delete a reservation",
        description = "Deletes a reservation. ADMIN only."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reservation deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN privileges required"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        reservationService.deleteReservation(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    private void validatePageable(Pageable pageable) {

        if (pageable.getPageNumber() < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative."
            );
        }

        if (pageable.getPageSize() < 1 || pageable.getPageSize() > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100."
            );
        }

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();

            if (!property.equals("id")
                    && !property.equals("price")
                    && !property.equals("status")
                    && !property.equals("startTime")
                    && !property.equals("endTime")) {

                throw new BadRequestException(
                        "Invalid sort field: " + property
                );
            }
        }
    }
}
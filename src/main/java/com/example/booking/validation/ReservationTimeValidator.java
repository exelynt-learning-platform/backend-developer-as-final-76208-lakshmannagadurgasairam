package com.example.booking.validation;

import com.example.booking.dto.CreateReservationRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationTimeValidator
        implements ConstraintValidator<ValidReservationTime, CreateReservationRequest> {

    @Override
    public boolean isValid(
            CreateReservationRequest request,
            ConstraintValidatorContext context) {

        if (request == null ||
            request.getStartTime() == null ||
            request.getEndTime() == null) {
            return true;
        }

        return request.getEndTime().isAfter(request.getStartTime());
    }
}
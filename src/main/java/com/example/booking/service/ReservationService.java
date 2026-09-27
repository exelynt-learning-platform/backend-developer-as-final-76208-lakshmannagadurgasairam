package com.example.booking.service;

import com.example.booking.dto.CreateReservationRequest;
import com.example.booking.dto.ReservationResponse;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;

    public ReservationResponse createReservation(
            CreateReservationRequest request,
            User currentUser) {

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: " + request.getResourceId()));

        Reservation reservation = Reservation.builder()
                .user(currentUser)
                .resource(resource)
                .price(request.getPrice())
                .status(ReservationStatus.PENDING)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return mapToResponse(reservationRepository.save(reservation));
    }

    public Page<ReservationResponse> getReservations(
            User currentUser,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        // ADMIN views all reservations; USER views only their own reservations
        Long filterUserId =
                (currentUser.getRole() == Role.ROLE_ADMIN)
                        ? null
                        : currentUser.getId();

        Specification<Reservation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterUserId != null) {
                predicates.add(
                        cb.equal(
                                root.get("user").get("id"),
                                filterUserId
                        )
                );
            }

            if (status != null) {
                predicates.add(
                        cb.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            if (minPrice != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        )
                );
            }

            if (maxPrice != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("price"),
                                maxPrice
                        )
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };

        return reservationRepository
                .findAll(spec, pageable)
                .map(this::mapToResponse);
    }

    public ReservationResponse updateReservationStatus(
            Long id,
            ReservationStatus status,
            User currentUser) {

        // Only ADMIN can update reservation status
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException(
                    "Only ADMIN can update reservation status."
            );
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with id: " + id));

        reservation.setStatus(status);

        return mapToResponse(
                reservationRepository.save(reservation)
        );
    }

    public void deleteReservation(
            Long id,
            User currentUser) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with id: " + id));

        // Only ADMIN can delete reservations
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException(
                    "Only ADMIN can delete reservations."
            );
        }

        reservationRepository.delete(reservation);
    }

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .username(reservation.getUser().getUsername())
                .resourceId(reservation.getResource().getId())
                .resourceName(reservation.getResource().getName())
                .price(reservation.getPrice())
                .status(reservation.getStatus())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .build();
    }
}
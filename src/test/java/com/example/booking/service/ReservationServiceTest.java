package com.example.booking.service;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.dto.CreateReservationRequest;
import com.example.booking.dto.ReservationResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ReservationService reservationService;


    // ---------------------------------------------------------
    // TEST USER
    // ---------------------------------------------------------

    private User user() {
        User user = new User();

        user.setId(2L);
        user.setUsername("user");
        user.setRole(Role.ROLE_USER);

        return user;
    }


    // ---------------------------------------------------------
    // TEST ADMIN
    // ---------------------------------------------------------

    private User admin() {
        User admin = new User();

        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole(Role.ROLE_ADMIN);

        return admin;
    }


    // ---------------------------------------------------------
    // TEST RESOURCE
    // ---------------------------------------------------------

    private Resource resource() {
        Resource resource = new Resource();

        resource.setId(1L);
        resource.setName("Conference Room");
        resource.setDescription("Main conference room");

        return resource;
    }


    // ---------------------------------------------------------
    // TEST RESERVATION
    // ---------------------------------------------------------

    private Reservation reservation(User user) {

        return Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource())
                .price(new BigDecimal("500.00"))
                .status(ReservationStatus.PENDING)
                .startTime(LocalDateTime.of(2026, 10, 1, 10, 0))
                .endTime(LocalDateTime.of(2026, 10, 1, 12, 0))
                .build();
    }


    // ---------------------------------------------------------
    // CREATE RESERVATION
    // ---------------------------------------------------------

    @Test
    void createReservation_shouldUseAuthenticatedUser() {

        User currentUser = user();

        CreateReservationRequest request = new CreateReservationRequest();

        request.setResourceId(1L);
        request.setPrice(new BigDecimal("500.00"));
        request.setStatus(ReservationStatus.PENDING);
        request.setStartTime(LocalDateTime.of(2026, 10, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2026, 10, 1, 12, 0));


        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource()));


        Reservation savedReservation = reservation(currentUser);

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(savedReservation);


        ReservationResponse response =
                reservationService.createReservation(request, currentUser);


        assertNotNull(response);

        assertEquals(2L, response.getUserId());
        assertEquals("user", response.getUsername());
        assertEquals(1L, response.getResourceId());
        assertEquals(new BigDecimal("500.00"), response.getPrice());
        assertEquals(ReservationStatus.PENDING, response.getStatus());


        ArgumentCaptor<Reservation> captor =
                ArgumentCaptor.forClass(Reservation.class);

        verify(reservationRepository).save(captor.capture());


        Reservation saved = captor.getValue();

        assertEquals(currentUser, saved.getUser());
        assertEquals(1L, saved.getResource().getId());
    }


    // ---------------------------------------------------------
    // CREATE RESERVATION - RESOURCE NOT FOUND
    // ---------------------------------------------------------

    @Test
    void createReservation_shouldThrowWhenResourceNotFound() {

        User currentUser = user();

        CreateReservationRequest request = new CreateReservationRequest();

        request.setResourceId(999L);
        request.setPrice(new BigDecimal("500.00"));
        request.setStatus(ReservationStatus.PENDING);
        request.setStartTime(LocalDateTime.of(2026, 10, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2026, 10, 1, 12, 0));


        when(resourceRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request, currentUser)
        );


        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }


    // ---------------------------------------------------------
    // GET RESERVATIONS - USER
    // ---------------------------------------------------------

    @Test
    void getReservations_shouldReturnUserReservations() {

        User currentUser = user();

        Pageable pageable =
                PageRequest.of(
                        0,
                        10,
                        Sort.by(Sort.Direction.DESC, "id")
                );


        Reservation reservation = reservation(currentUser);

        Page<Reservation> reservationPage =
                new PageImpl<>(
                        List.of(reservation),
                        pageable,
                        1
                );


        when(reservationRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(reservationPage);


        Page<ReservationResponse> result =
                reservationService.getReservations(
                        currentUser,
                        ReservationStatus.PENDING,
                        new BigDecimal("100"),
                        new BigDecimal("1000"),
                        pageable
                );


        assertNotNull(result);

        assertEquals(1, result.getTotalElements());

        assertEquals(
                2L,
                result.getContent().get(0).getUserId()
        );

        assertEquals(
                "user",
                result.getContent().get(0).getUsername()
        );


        verify(reservationRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );
    }


    // ---------------------------------------------------------
    // GET RESERVATIONS - ADMIN
    // ---------------------------------------------------------

    @Test
    void getReservations_shouldReturnAllReservationsForAdmin() {

        User currentAdmin = admin();

        Pageable pageable =
                PageRequest.of(
                        0,
                        10,
                        Sort.by(Sort.Direction.DESC, "id")
                );


        Reservation reservation1 = reservation(user());

        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("anotherUser");
        anotherUser.setRole(Role.ROLE_USER);

        Reservation reservation2 = reservation(anotherUser);


        Page<Reservation> reservationPage =
                new PageImpl<>(
                        List.of(reservation1, reservation2),
                        pageable,
                        2
                );


        when(reservationRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(reservationPage);


        Page<ReservationResponse> result =
                reservationService.getReservations(
                        currentAdmin,
                        null,
                        null,
                        null,
                        pageable
                );


        assertNotNull(result);

        assertEquals(2, result.getTotalElements());

        assertEquals(
                2L,
                result.getContent().get(0).getUserId()
        );

        assertEquals(
                3L,
                result.getContent().get(1).getUserId()
        );


        verify(reservationRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );
    }


    // ---------------------------------------------------------
    // DELETE - USER SHOULD BE DENIED
    // ---------------------------------------------------------

    @Test
    void deleteReservation_shouldDenyUser() {

        User currentUser = user();

        Reservation reservation = reservation(currentUser);


        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));


        assertThrows(
                AccessDeniedException.class,
                () -> reservationService.deleteReservation(
                        1L,
                        currentUser
                )
        );


        verify(reservationRepository, never())
                .delete(any(Reservation.class));
    }


    // ---------------------------------------------------------
    // DELETE - ADMIN
    // ---------------------------------------------------------

    @Test
    void deleteReservation_shouldAllowAdmin() {

        User currentAdmin = admin();

        Reservation reservation = reservation(user());


        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));


        doNothing()
                .when(reservationRepository)
                .delete(reservation);


        reservationService.deleteReservation(
                1L,
                currentAdmin
        );


        verify(reservationRepository)
                .delete(reservation);
    }


    // ---------------------------------------------------------
    // DELETE - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void deleteReservation_shouldThrowWhenNotFound() {

        User currentAdmin = admin();


        when(reservationRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.deleteReservation(
                        999L,
                        currentAdmin
                )
        );


        verify(reservationRepository, never())
                .delete(any(Reservation.class));
    }


    // ---------------------------------------------------------
    // UPDATE STATUS - ADMIN
    // ---------------------------------------------------------

    @Test
    void updateReservationStatus_shouldUpdateStatusForAdmin() {

        User currentAdmin = admin();

        Reservation reservation = reservation(user());


        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(reservation);


        ReservationResponse result =
                reservationService.updateReservationStatus(
                        1L,
                        ReservationStatus.CONFIRMED,
                        currentAdmin
                );


        assertNotNull(result);

        assertEquals(
                ReservationStatus.CONFIRMED,
                reservation.getStatus()
        );


        verify(reservationRepository)
                .save(reservation);
    }


    // ---------------------------------------------------------
    // UPDATE STATUS - USER'S OWN RESERVATION
    // ---------------------------------------------------------

    @Test
    void updateReservationStatus_shouldAllowOwnReservation() {

        User currentUser = user();

        Reservation reservation = reservation(currentUser);


        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(reservation);


        ReservationResponse result =
                reservationService.updateReservationStatus(
                        1L,
                        ReservationStatus.CANCELLED,
                        currentUser
                );


        assertNotNull(result);

        assertEquals(
                ReservationStatus.CANCELLED,
                reservation.getStatus()
        );


        verify(reservationRepository)
                .save(reservation);
    }


    // ---------------------------------------------------------
    // UPDATE STATUS - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void updateReservationStatus_shouldThrowWhenNotFound() {

        User currentAdmin = admin();


        when(reservationRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.updateReservationStatus(
                        999L,
                        ReservationStatus.CONFIRMED,
                        currentAdmin
                )
        );


        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }
}
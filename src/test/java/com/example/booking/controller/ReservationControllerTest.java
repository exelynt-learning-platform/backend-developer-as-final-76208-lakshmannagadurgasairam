package com.example.booking.controller;

import com.example.booking.config.JwtService;
import com.example.booking.dto.CreateReservationRequest;
import com.example.booking.dto.ReservationResponse;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.User;
import com.example.booking.service.ReservationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtService jwtService;


    // ---------------------------------------------------------
    // TEST USER
    // ---------------------------------------------------------

    private User testUser() {
        User user = new User();
        user.setId(2L);
        user.setUsername("user");
        return user;
    }


    // ---------------------------------------------------------
    // TEST AUTHENTICATION
    // ---------------------------------------------------------

    private Authentication testAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                testUser(),
                null,
                List.of()
        );
    }


    // ---------------------------------------------------------
    // CREATE RESERVATION
    // ---------------------------------------------------------

    @Test
    void createReservation_shouldReturn201() throws Exception {

        ReservationResponse response = ReservationResponse.builder()
                .id(1L)
                .userId(2L)
                .username("user")
                .resourceId(1L)
                .resourceName("Conference Room")
                .price(new BigDecimal("500.00"))
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationService.createReservation(
                any(),
                any()
        )).thenReturn(response);


        mockMvc.perform(
                post("/api/reservations")
                        .with(authentication(testAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "resourceId": 1,
                                "price": 500.00,
                                "status": "PENDING"
                            }
                        """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(2))
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.resourceId").value(1))
        .andExpect(jsonPath("$.resourceName").value("Conference Room"))
        .andExpect(jsonPath("$.price").value(500.00))
        .andExpect(jsonPath("$.status").value("PENDING"));
    }


    // ---------------------------------------------------------
    // GET RESERVATIONS
    // ---------------------------------------------------------

    @Test
    void getReservations_shouldReturnPage() throws Exception {

        ReservationResponse reservation = ReservationResponse.builder()
                .id(1L)
                .userId(2L)
                .username("user")
                .resourceId(1L)
                .resourceName("Conference Room")
                .price(new BigDecimal("500.00"))
                .status(ReservationStatus.PENDING)
                .build();


        PageImpl<ReservationResponse> page =
                new PageImpl<>(
                        List.of(reservation),
                        PageRequest.of(
                                0,
                                10,
                                Sort.by(Sort.Direction.DESC, "id")
                        ),
                        1
                );


        when(reservationService.getReservations(
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(page);


        mockMvc.perform(
                get("/api/reservations")
                        .with(authentication(testAuthentication()))
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "PENDING")
                        .param("minPrice", "100")
                        .param("maxPrice", "1000")
                        .param("sort", "id,desc")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].username").value("user"))
        .andExpect(jsonPath("$.content[0].resourceName").value("Conference Room"))
        .andExpect(jsonPath("$.content[0].price").value(500.00))
        .andExpect(jsonPath("$.content[0].status").value("PENDING"))
        .andExpect(jsonPath("$.pageable.pageNumber").value(0))
        .andExpect(jsonPath("$.pageable.pageSize").value(10))
        .andExpect(jsonPath("$.totalElements").value(1));
    }


    // ---------------------------------------------------------
    // UPDATE RESERVATION STATUS
    // ---------------------------------------------------------

    @Test
    void updateReservationStatus_shouldReturn200() throws Exception {

        ReservationResponse response = ReservationResponse.builder()
                .id(1L)
                .userId(2L)
                .username("user")
                .resourceId(1L)
                .resourceName("Conference Room")
                .price(new BigDecimal("500.00"))
                .status(ReservationStatus.CONFIRMED)
                .build();


        when(reservationService.updateReservationStatus(
                any(),
                any(),
                any()
        )).thenReturn(response);


        mockMvc.perform(
                patch("/api/reservations/1/status")
                        .with(authentication(testAuthentication()))
                        .param("status", "CONFIRMED")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }


    // ---------------------------------------------------------
    // DELETE RESERVATION
    // ---------------------------------------------------------

    @Test
    void deleteReservation_shouldReturn204() throws Exception {

        mockMvc.perform(
                delete("/api/reservations/1")
                        .with(authentication(testAuthentication()))
        )
        .andExpect(status().isNoContent());
    }
}
package com.example.booking.controller;

import com.example.booking.dto.AuthResponse;
import com.example.booking.service.AuthService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private com.example.booking.config.JwtService jwtService;

    @Test
    void login_shouldReturnToken() throws Exception {

        AuthResponse response = AuthResponse.builder()
                .token("test-token")
                .username("user")
                .role("ROLE_USER")
                .build();

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "user",
                                "password": "user123"
                            }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("test-token"))
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }
}
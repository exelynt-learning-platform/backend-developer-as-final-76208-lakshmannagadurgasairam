package com.example.booking.service;

import com.example.booking.config.JwtService;
import com.example.booking.dto.AuthRequest;
import com.example.booking.dto.AuthResponse;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;


    // ---------------------------------------------------------
    // LOGIN
    // ---------------------------------------------------------

    @Test
    void login_shouldReturnTokenAndUserDetails() {

        AuthRequest request = new AuthRequest();

        request.setUsername("user");
        request.setPassword("user123");


        User user = new User();

        user.setId(2L);
        user.setUsername("user");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_USER);


        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(null);


        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));


        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");


        AuthResponse response =
                authService.login(request);


        assertNotNull(response);

        assertEquals(
                "test-jwt-token",
                response.getToken()
        );

        assertEquals(
                "user",
                response.getUsername()
        );

        assertEquals(
                "ROLE_USER",
                response.getRole()
        );


        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(userRepository)
                .findByUsername("user");

        verify(jwtService)
                .generateToken(user);
    }


    // ---------------------------------------------------------
    // ADMIN LOGIN
    // ---------------------------------------------------------

    @Test
    void login_shouldReturnAdminRole() {

        AuthRequest request = new AuthRequest();

        request.setUsername("admin");
        request.setPassword("admin123");


        User admin = new User();

        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("encodedPassword");
        admin.setRole(Role.ROLE_ADMIN);


        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(null);


        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));


        when(jwtService.generateToken(admin))
                .thenReturn("admin-jwt-token");


        AuthResponse response =
                authService.login(request);


        assertNotNull(response);

        assertEquals(
                "admin-jwt-token",
                response.getToken()
        );

        assertEquals(
                "admin",
                response.getUsername()
        );

        assertEquals(
                "ROLE_ADMIN",
                response.getRole()
        );


        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(userRepository)
                .findByUsername("admin");

        verify(jwtService)
                .generateToken(admin);
    }
}
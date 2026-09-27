package com.example.booking.controller;

import com.example.booking.dto.ResourceRequest;
import com.example.booking.dto.ResourceResponse;
import com.example.booking.service.ResourceService;
import com.example.booking.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ResourceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private com.example.booking.config.JwtService jwtService;

    @Test
    void getAllResources_shouldReturnResources() throws Exception {

        ResourceResponse resource = ResourceResponse.builder()
                .id(1L)
                .name("Conference Room")
                .description("Meeting room")
                .build();

        PageImpl<ResourceResponse> page =
                new PageImpl<>(
                        List.of(resource),
                        PageRequest.of(0, 10),
                        1
                );

        when(resourceService.getAllResources(any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Conference Room"))
                .andExpect(jsonPath("$.content[0].description").value("Meeting room"));
    }

    @Test
    void getResourceById_shouldReturnResource() throws Exception {

        ResourceResponse resource = ResourceResponse.builder()
                .id(1L)
                .name("Conference Room")
                .description("Meeting room")
                .build();

        when(resourceService.getResourceById(1L))
                .thenReturn(resource);

        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.description").value("Meeting room"));
    }

    @Test
    void getResourceById_whenNotFound_shouldReturn404() throws Exception {

        when(resourceService.getResourceById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Resource not found with id: 999"
                ));

        mockMvc.perform(get("/api/resources/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createResource_shouldReturn201() throws Exception {

        ResourceResponse response = ResourceResponse.builder()
                .id(1L)
                .name("Conference Room")
                .description("Meeting room")
                .build();

        when(resourceService.createResource(any(ResourceRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Conference Room",
                                "description": "Meeting room"
                            }
                        """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Conference Room"))
        .andExpect(jsonPath("$.description").value("Meeting room"));
    }

    @Test
    void updateResource_shouldReturn200() throws Exception {

        ResourceResponse response = ResourceResponse.builder()
                .id(1L)
                .name("Updated Room")
                .description("Updated description")
                .build();

        when(resourceService.updateResource(
                eq(1L),
                any(ResourceRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/api/resources/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Updated Room",
                                "description": "Updated description"
                            }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Updated Room"))
        .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void deleteResource_shouldReturn204() throws Exception {

        mockMvc.perform(delete("/api/resources/1"))
                .andExpect(status().isNoContent());
    }
}
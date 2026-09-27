package com.example.booking.service;

import com.example.booking.dto.ResourceRequest;
import com.example.booking.dto.ResourceResponse;
import com.example.booking.entity.Resource;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.ResourceRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;


    // ---------------------------------------------------------
    // TEST RESOURCE
    // ---------------------------------------------------------

    private Resource testResource() {
        Resource resource = new Resource();

        resource.setId(1L);
        resource.setName("Conference Room");
        resource.setDescription("Main conference room");

        return resource;
    }


    // ---------------------------------------------------------
    // GET ALL RESOURCES
    // ---------------------------------------------------------

    @Test
    void getAllResources_shouldReturnResources() {

        Resource resource = testResource();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Resource> page =
                new PageImpl<>(
                        List.of(resource),
                        pageable,
                        1
                );

        when(resourceRepository.findAll(pageable))
                .thenReturn(page);


        Page<ResourceResponse> result =
                resourceService.getAllResources(pageable);


        assertNotNull(result);

        assertEquals(1, result.getTotalElements());

        assertEquals(
                1L,
                result.getContent().get(0).getId()
        );

        assertEquals(
                "Conference Room",
                result.getContent().get(0).getName()
        );

        assertEquals(
                "Main conference room",
                result.getContent().get(0).getDescription()
        );


        verify(resourceRepository)
                .findAll(pageable);
    }


    // ---------------------------------------------------------
    // GET RESOURCE BY ID
    // ---------------------------------------------------------

    @Test
    void getResourceById_shouldReturnResource() {

        Resource resource = testResource();

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));


        ResourceResponse result =
                resourceService.getResourceById(1L);


        assertNotNull(result);

        assertEquals(1L, result.getId());

        assertEquals(
                "Conference Room",
                result.getName()
        );

        assertEquals(
                "Main conference room",
                result.getDescription()
        );


        verify(resourceRepository)
                .findById(1L);
    }


    // ---------------------------------------------------------
    // GET RESOURCE BY ID - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void getResourceById_shouldThrowWhenNotFound() {

        when(resourceRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> resourceService.getResourceById(999L)
        );


        verify(resourceRepository)
                .findById(999L);
    }


    // ---------------------------------------------------------
    // CREATE RESOURCE
    // ---------------------------------------------------------

    @Test
    void createResource_shouldCreateResource() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Meeting Room");
        request.setDescription("Small meeting room");


        Resource savedResource = new Resource();

        savedResource.setId(2L);
        savedResource.setName("Meeting Room");
        savedResource.setDescription("Small meeting room");


        when(resourceRepository.save(any(Resource.class)))
                .thenReturn(savedResource);


        ResourceResponse result =
                resourceService.createResource(request);


        assertNotNull(result);

        assertEquals(2L, result.getId());

        assertEquals(
                "Meeting Room",
                result.getName()
        );

        assertEquals(
                "Small meeting room",
                result.getDescription()
        );


        verify(resourceRepository)
                .save(any(Resource.class));
    }


    // ---------------------------------------------------------
    // UPDATE RESOURCE
    // ---------------------------------------------------------

    @Test
    void updateResource_shouldUpdateResource() {

        Resource existingResource = testResource();

        ResourceRequest request = new ResourceRequest();

        request.setName("Updated Room");
        request.setDescription("Updated description");


        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(existingResource));

        when(resourceRepository.save(any(Resource.class)))
                .thenReturn(existingResource);


        ResourceResponse result =
                resourceService.updateResource(
                        1L,
                        request
                );


        assertNotNull(result);

        assertEquals(
                "Updated Room",
                result.getName()
        );

        assertEquals(
                "Updated description",
                result.getDescription()
        );


        assertEquals(
                "Updated Room",
                existingResource.getName()
        );

        assertEquals(
                "Updated description",
                existingResource.getDescription()
        );


        verify(resourceRepository)
                .findById(1L);

        verify(resourceRepository)
                .save(existingResource);
    }


    // ---------------------------------------------------------
    // UPDATE RESOURCE - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void updateResource_shouldThrowWhenNotFound() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Updated Room");
        request.setDescription("Updated description");


        when(resourceRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> resourceService.updateResource(
                        999L,
                        request
                )
        );


        verify(resourceRepository)
                .findById(999L);

        verify(resourceRepository, never())
                .save(any(Resource.class));
    }


    // ---------------------------------------------------------
    // DELETE RESOURCE
    // ---------------------------------------------------------

    @Test
    void deleteResource_shouldDeleteResource() {

        when(resourceRepository.existsById(1L))
                .thenReturn(true);


        resourceService.deleteResource(1L);


        verify(resourceRepository)
                .existsById(1L);

        verify(resourceRepository)
                .deleteById(1L);
    }


    // ---------------------------------------------------------
    // DELETE RESOURCE - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void deleteResource_shouldThrowWhenNotFound() {

        when(resourceRepository.existsById(999L))
                .thenReturn(false);


        assertThrows(
                ResourceNotFoundException.class,
                () -> resourceService.deleteResource(999L)
        );


        verify(resourceRepository)
                .existsById(999L);

        verify(resourceRepository, never())
                .deleteById(999L);
    }
}
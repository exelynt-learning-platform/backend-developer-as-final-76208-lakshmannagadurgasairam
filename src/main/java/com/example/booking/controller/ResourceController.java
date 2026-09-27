package com.example.booking.controller;

import com.example.booking.dto.ResourceRequest;
import com.example.booking.dto.ResourceResponse;
import com.example.booking.exception.BadRequestException;
import com.example.booking.service.ResourceService;

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

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(
        summary = "Get all resources",
        description = "Returns a paginated list of resources. USER and ADMIN can access this endpoint."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<Page<ResourceResponse>> getAllResources(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        validatePageable(pageable);

        return ResponseEntity.ok(
            resourceService.getAllResources(pageable)
        );
    }

    @Operation(
        summary = "Get resource by ID",
        description = "Returns a single resource by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            resourceService.getResourceById(id)
        );
    }

    @Operation(
        summary = "Create a resource",
        description = "Creates a new resource. ADMIN only."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Resource created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid resource data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN privileges required")
    })
    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(
            @Valid @RequestBody ResourceRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createResource(request));
    }

    @Operation(
        summary = "Update a resource",
        description = "Updates an existing resource. ADMIN only."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid resource data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN privileges required"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {

        return ResponseEntity.ok(
            resourceService.updateResource(id, request)
        );
    }

    @Operation(
        summary = "Delete a resource",
        description = "Deletes an existing resource. ADMIN only."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN privileges required"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id) {

        resourceService.deleteResource(id);
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

            if (!property.equals("id") && !property.equals("name")) {
                throw new BadRequestException(
                        "Invalid sort field: " + property
                );
            }
        }
    }
}

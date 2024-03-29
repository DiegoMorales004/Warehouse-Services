package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.ServiceDomain;
import com.diegomorales.warehouse.dto.ServiceDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.service.ServiceDomainService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/service")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class ServiceController {

    private ServiceDomainService service;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody ServiceDTO dto) throws GenericException, BadRequestException{
        var response = this.service.save(dto);
        return ResponseEntity.created(URI.create("/api/service/" + response.getId())).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable Integer id) throws GenericException, BadRequestException{
        var response = this.service.findOne(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @Valid @RequestBody ServiceDTO dto) throws GenericException, BadRequestException{
        var response = this.service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Integer id) throws GenericException, BadRequestException, DataIntegrityViolationException {
        this.service.deleteService(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ServiceDomain>> findAll(Pageable page, @RequestParam(value = "search", required = false) String search) throws NoContentException, GenericException{
        var response = this.service.findAll(search, page);
        return ResponseEntity.ok(response);
    }

}

package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.Role;
import com.diegomorales.warehouse.dto.RoleDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.service.RoleService;
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
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/role")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService service;
    @PostMapping
    public ResponseEntity<Object> save( @Valid @RequestBody RoleDTO dto) throws GenericException, BadRequestException {
        var response = this.service.save(dto);
        return ResponseEntity.created(URI.create("/api/role/" + response.getId())).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") Integer id) throws GenericException, BadRequestException{
        var response = this.service.findOne(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @Valid @RequestBody RoleDTO dto) throws GenericException, BadRequestException{
        var response = this.service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws GenericException, BadRequestException, DataIntegrityViolationException {
        this.service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<Role>> findAll(Pageable page,@RequestParam(value = "search", required = false) String search) throws GenericException, NoContentException {
        var response = this.service.findAll(search, page);
        return ResponseEntity.ok(response);
    }

}

package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.Branch;
import com.diegomorales.warehouse.dto.BranchDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.service.BranchService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "api/branch")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class BranchController {

    private final BranchService service;

    @PostMapping
    public ResponseEntity<Object> save( @Valid @RequestBody BranchDTO dto) throws BadRequestException, GenericException{
        var response = this.service.save(dto);
        return ResponseEntity.created(URI.create("/api/branch/" + response.getId())).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") Integer id) throws GenericException, BadRequestException{
        var response = this.service.findOne(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @Valid @RequestBody BranchDTO dto) throws GenericException, BadRequestException{
        var response = this.service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<Object> disable(@PathVariable("id") Integer id) throws GenericException, BadRequestException{
        var response = this.service.disable(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Branch>> findAll(Pageable page, @RequestParam(value = "search", required = false) String search) throws NoContentException, GenericException{
        var response = this.service.findAll(search, page);
        return ResponseEntity.ok(response);
    }

}

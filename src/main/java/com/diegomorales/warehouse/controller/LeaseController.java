package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.dto.LeaseDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.service.LeaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/lease")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class LeaseController {

    private final LeaseService service;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody LeaseDTO dto) throws GenericException, BadRequestException{
        var response = this.service.save(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne( @PathVariable("id") Integer id) throws GenericException, BadRequestException{
        var response = this.service.findOne(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete( @PathVariable("id") Integer id) throws GenericException, BadRequestException{
        this.service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @RequestBody LeaseDTO dto) throws BadRequestException, GenericException{
        var response = this.service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<LeaseDTO>> findAll(@RequestParam(value = "search", required = false) Integer id_user, Pageable page) throws NoContentException, GenericException{
        var response = this.service.findAll(id_user, page);
        return ResponseEntity.ok(response);
    }

}

package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.UserDomain;
import com.diegomorales.warehouse.dto.ErrorDTO;
import com.diegomorales.warehouse.dto.UserDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.service.UserService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/user")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody UserDTO dto) throws GenericException, BadRequestException, ResponseStatusException {
        var response = this.service.save(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> saveWithDefaultRole(@RequestBody UserDTO dto) throws GenericException, BadRequestException {
        UserDomain response = this.service.saveWithDefaultRole(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") Integer id) throws GenericException, BadRequestException {
        var response = this.service.findOne(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody UserDTO dto) throws GenericException, BadRequestException {
        this.service.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) throws GenericException, BadRequestException {
        var response = this.service.delete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(@RequestParam(value = "search", required = false) String search, Pageable page) throws GenericException, NoContentException {
        var response = this.service.findAll(search, page);
        return ResponseEntity.ok(response);
    }

}

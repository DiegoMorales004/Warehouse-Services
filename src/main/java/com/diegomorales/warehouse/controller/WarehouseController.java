package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.dto.WarehouseDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.service.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/warehouse")
@AllArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody WarehouseDTO dto) throws GenericException, BadRequestException {
        var response = this.service.save(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") Integer id) throws GenericException, BadRequestException{
        var response = this.service.findOne(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @RequestBody WarehouseDTO dto) throws BadRequestException, GenericException{
        var response = this.service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws GenericException, BadRequestException{
        this.service.delete(id);
        return ResponseEntity.ok().build();
    }

}

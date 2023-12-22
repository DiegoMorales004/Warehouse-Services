package com.diegomorales.warehouse.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceWarehouseId implements Serializable {
    private Integer id_warehouse;
    private Integer id_service;
}

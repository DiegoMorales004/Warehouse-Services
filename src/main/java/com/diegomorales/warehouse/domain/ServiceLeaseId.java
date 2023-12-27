package com.diegomorales.warehouse.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceLeaseId implements Serializable {
    private Integer id_lease;
    private Integer id_service;
}

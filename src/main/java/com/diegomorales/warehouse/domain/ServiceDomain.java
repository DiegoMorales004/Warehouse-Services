package com.diegomorales.warehouse.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "services")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceDomain {

    static final String SEQ_NAME = "SEQ_SERVICES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Integer id;
    private String name;
    private String description;
    private Double price;
}

package com.diegomorales.warehouse.domain;

import jakarta.persistence.*;

@Entity(name = "warehouses")
public class Warehouse {

    static final String SEQ_NAME = "SEQ_WAREHOUSES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Integer id;
    private String code;

}

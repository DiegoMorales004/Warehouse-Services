package com.diegomorales.warehouse.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "warehouses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Warehouse {

    static final String SEQ_NAME = "SEQ_WAREHOUSES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Integer id;
    private String code;
    private String description;
    private Boolean available;
    private String size;
    private Double price;
    private Integer idBranch;
}

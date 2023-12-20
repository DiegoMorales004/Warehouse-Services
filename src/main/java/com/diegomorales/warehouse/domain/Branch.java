package com.diegomorales.warehouse.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "branches")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Branch {

    static final String SEQ_NAME = "SEQ_BRANCHES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Integer id;
    private String name;
    private String code;
    private String location;
    private Boolean parking;
}

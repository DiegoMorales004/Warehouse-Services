package com.diegomorales.warehouse.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "leases")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Lease {

    static final String SEQ_NAME = "SEQ_LEASES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Integer id;
    private Integer id_user;
    private Integer id_warehouse;
    private Double total;
}

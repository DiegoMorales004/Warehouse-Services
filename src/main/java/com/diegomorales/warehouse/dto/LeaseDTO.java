package com.diegomorales.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LeaseDTO implements Serializable {
    private Integer id;
    private Integer id_user;
    private Integer id_warehouse;
    private List<String> extraServices;
}

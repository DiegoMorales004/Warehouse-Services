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
public class WarehouseDTO implements Serializable {
    private Integer id;
    private String code;
    private String description;
    private Boolean available;
    private String size;
    private Double price;
    private Integer id_branch;
    private Integer id_user;
    private List<String> extraServices;
}

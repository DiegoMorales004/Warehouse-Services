package com.diegomorales.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BranchDTO implements Serializable {
    private Integer id;
    private String name;
    private String code;
    private String location;
    private Boolean parking;
}

package com.diegomorales.warehouse.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class WarehouseDTO implements Serializable {
    private Integer id;
    @NotNull
    @NotEmpty
    private String code;
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    @NotEmpty
    private String size;
    @NotNull
    private Double price;
    @NotNull
    private Integer idBranch;
    private List<String> extraServices;
}

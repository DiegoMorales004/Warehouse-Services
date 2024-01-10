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
public class LeaseDTO implements Serializable {
    private Integer id;
    @NotNull
    @NotEmpty
    private Integer idUser;
    @NotNull
    @NotEmpty
    private Integer idWarehouse;
    private Double total;
    @NotNull
    private Boolean active;
    private List<String> extraServices;
}

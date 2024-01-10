package com.diegomorales.warehouse.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class ServiceDTO implements Serializable {
    private Integer id;
    @NotNull
    @NotEmpty
    private String name;
    private String description;
    @NotNull
    @NotEmpty
    private Double price;
}

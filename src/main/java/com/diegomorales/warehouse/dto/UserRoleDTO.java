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
public class UserRoleDTO implements Serializable {
    private Integer id_user;
    private Integer id_rol;
}

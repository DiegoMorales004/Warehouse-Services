package com.diegomorales.warehouse.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Entity(name = "users_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRole implements Serializable {
    @EmbeddedId
    private UserRoleId userRoleId;
}

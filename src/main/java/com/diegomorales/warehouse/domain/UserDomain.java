package com.diegomorales.warehouse.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDomain {

    static final String SEQ_NAME = "SEQ_USERS";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Integer id;
    private String username;
    private String email;
    private String password;
    private Boolean active;
}

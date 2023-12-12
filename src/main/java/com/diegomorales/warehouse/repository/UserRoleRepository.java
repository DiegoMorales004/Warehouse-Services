package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.UserRole;
import com.diegomorales.warehouse.domain.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    @Query("SELECT ur FROM users_roles ur WHERE ur.userRoleId.id_user = ?1")
    List<UserRole> findByUserId(Integer id_user);
}

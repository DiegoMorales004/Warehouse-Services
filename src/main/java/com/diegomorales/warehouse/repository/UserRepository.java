package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.UserDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDomain, Integer> {
    Optional<UserDomain> findFirstByEmailContainsIgnoreCase(String email);

    Optional<UserDomain> findFirstByUsernameIgnoreCase(String username);

    Page<UserDomain> findAllByUsernameContainsIgnoreCase(String search, Pageable page);
}

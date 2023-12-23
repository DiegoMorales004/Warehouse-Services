package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.ServiceDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceDomain, Integer> {
    Optional<ServiceDomain> findFirstByNameIgnoreCase(String name);
    Page<ServiceDomain> findAllByNameNotContainsIgnoreCase(String search, Pageable page);
}

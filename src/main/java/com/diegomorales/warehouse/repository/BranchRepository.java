package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    Optional<Branch> findFirstByNameIgnoreCase(String name);
    Optional<Branch> findFirstByCodeIgnoreCase(String code);

    Page<Branch> findAllByNameContainsIgnoreCase(String search, Pageable page);

}

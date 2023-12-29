package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    Optional<Warehouse> findFirstByCodeIgnoreCase(String code);

    Page<Warehouse> findAllByCodeContainsIgnoreCase(String code, Pageable page);

    List<Warehouse> findAllByIdBranch(Integer idBranch);
}

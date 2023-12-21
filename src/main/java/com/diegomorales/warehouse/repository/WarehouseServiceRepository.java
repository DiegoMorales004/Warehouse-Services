package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.WarehouseService;
import com.diegomorales.warehouse.domain.WarehouseServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseServiceRepository extends JpaRepository<WarehouseService, WarehouseServiceId> {
}

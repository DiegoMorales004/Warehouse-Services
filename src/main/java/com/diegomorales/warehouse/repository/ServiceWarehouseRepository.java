package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.ServiceWarehouse;
import com.diegomorales.warehouse.domain.ServiceWarehouseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceWarehouseRepository extends JpaRepository<ServiceWarehouse, ServiceWarehouseId> {
}

package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.ServiceWarehouse;
import com.diegomorales.warehouse.domain.ServiceWarehouseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceWarehouseRepository extends JpaRepository<ServiceWarehouse, ServiceWarehouseId> {
    @Query("SELECT ws FROM warehouses_services ws WHERE ws.serviceWarehouseId.id_warehouse = ?1")
    List<ServiceWarehouse> findAllByWarehouseId(Integer id);

}

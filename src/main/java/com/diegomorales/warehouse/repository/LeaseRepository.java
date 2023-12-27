package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.Lease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Integer> {

    @Query("SELECT ls FROM leases ls WHERE ls.id_warehouse = ?1 AND ls.id_user = ?2")
    Optional<Lease> findFirstByIdUserAndIdWarehouse(Integer id_warehouse, Integer id_user);

}
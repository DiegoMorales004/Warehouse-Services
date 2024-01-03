package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.Lease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Integer> {

    Page<Lease> findAllByIdUser(Integer id_user, Pageable page);

    List<Lease> findAllByIdUser(Integer id);

    @Query("SELECT ls FROM leases ls WHERE ls.idWarehouse = ?1 AND ls.idUser = ?2")
    Optional<Lease> findByIdUserAndIdWarehouse(Integer id_warehouse, Integer id_user);

    Optional<Lease> findFirstByIdWarehouse(Integer idWarehouse);

}
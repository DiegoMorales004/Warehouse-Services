package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.Lease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Integer> {

}
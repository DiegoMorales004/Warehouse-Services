package com.diegomorales.warehouse.repository;

import com.diegomorales.warehouse.domain.ServiceLease;
import com.diegomorales.warehouse.domain.ServiceLeaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceLeaseRepository extends JpaRepository<ServiceLease, ServiceLeaseId> {

    @Query("SELECT ls FROM leases_services ls WHERE ls.serviceLeaseId.id_lease = ?1")
    List<ServiceLease> findAllByIdLease(Integer idLease);

}

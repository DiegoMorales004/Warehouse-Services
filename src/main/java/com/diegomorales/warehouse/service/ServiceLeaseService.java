package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.ServiceDomain;
import com.diegomorales.warehouse.domain.ServiceLease;
import com.diegomorales.warehouse.domain.ServiceLeaseId;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.repository.ServiceLeaseRepository;
import com.diegomorales.warehouse.repository.ServiceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ServiceLeaseService {

    private final ServiceLeaseRepository repository;
    private final ServiceRepository serviceRepository;

    private final ServiceDomainService serviceDomainService;

    public ServiceLease save(ServiceLeaseId id) throws GenericException, BadRequestException {
        try {

            this.serviceDomainService.findOne(id.getId_service());

            ServiceLease entity = new ServiceLease(id);

            return this.repository.save(entity);

        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(ServiceLeaseId id) throws GenericException, BadRequestException {
        try {

            Optional<ServiceLease> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The serviceLease with this id " + id + " does not exist");
            }
            this.repository.delete(valid.get());

        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void saveAllServicesByLease(List<Integer> listIds, Integer id_lease) throws GenericException, BadRequestException {

        for (Integer idService : listIds) {

            ServiceLeaseId serviceLeaseId = new ServiceLeaseId();

            serviceLeaseId.setId_lease(id_lease);
            serviceLeaseId.setId_service(idService);

            this.save(serviceLeaseId);

        }

    }

    public List<ServiceDomain> findServicesByIdLease(Integer id) throws GenericException, BadRequestException {
        try {

            List<ServiceDomain> serviceDomains = new ArrayList<>();

            List<ServiceLease> serviceLeases = this.repository.findAllByIdLease(id);

            serviceLeases.forEach(
                    serviceLease -> {
                        Optional<ServiceDomain> validService = this.serviceRepository.findById(serviceLease.getServiceLeaseId().getId_service());
                        if (validService.isEmpty()) {
                            try {
                                this.delete(serviceLease.getServiceLeaseId());
                            } catch (GenericException | BadRequestException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            serviceDomains.add(validService.get());
                        }
                    }
            );

            return serviceDomains;

        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void deleteAllServicesByLease(List<Integer> idsServices, Integer idLease) throws BadRequestException, GenericException {
        for (Integer idsService : idsServices) {

            ServiceLeaseId id = new ServiceLeaseId();
            id.setId_lease(idLease);
            id.setId_service(idsService);

            this.delete(id);

        }
    }

}

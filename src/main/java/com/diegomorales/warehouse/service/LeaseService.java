package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.*;
import com.diegomorales.warehouse.dto.LeaseDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.repository.LeaseRepository;
import com.diegomorales.warehouse.repository.UserRepository;
import com.diegomorales.warehouse.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class LeaseService {
    private final LeaseRepository repository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    private final ServiceLeaseService serviceLeaseService;
    private final ServiceDomainService serviceDomainService;
    private final WarehouseService warehouseService;

    public Lease save(LeaseDTO dto) throws GenericException, BadRequestException {
        try {

            Warehouse warehouse = warehouseValid(dto.getIdWarehouse());

            userValid(dto.getIdUser());

            Optional<Lease> validRecord = this.repository.findFirstByIdUserAndIdWarehouse(dto.getIdUser(), dto.getIdWarehouse());

            if (validRecord.isPresent()) {
                throw new BadRequestException("The user already occupies this warehouse.");
            }

            Lease entity = new Lease();
            BeanUtils.copyProperties(dto, entity, "id", "total");

            Lease saved;

            if (!dto.getExtraServices().isEmpty()) {

                List<ServiceDomain> extraServicesDomain = this.serviceDomainService.findServicesByName(dto.getExtraServices());
                List<Integer> extraServices = new ArrayList<>(
                        extraServicesDomain.stream().map(
                                ServiceDomain::getId
                        ).toList()
                );

                Double total = calculateTotal(warehouse.getPrice(), extraServicesDomain);
                entity.setTotal(total);

                saved = this.repository.save(entity);

                this.serviceLeaseService.saveAllServicesByLease(extraServices, saved.getId());

            } else {
                entity.setTotal(warehouse.getPrice());
                saved = this.repository.save(entity);
            }

            return saved;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public LeaseDTO findOne(Integer id) throws GenericException, BadRequestException {
        try {

            Optional<Lease> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The lease with this id does not exist");
            }

            LeaseDTO dto = new LeaseDTO();
            BeanUtils.copyProperties(valid.get(), dto);

            List<String> extraServices = findExtraServiceNamesByLease(id);

            dto.setExtraServices(extraServices);

            return dto;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public LeaseDTO update(Integer id, LeaseDTO dto) throws BadRequestException, GenericException {
        try {

            LeaseDTO oldLease = this.findOne(id);

            userValid(dto.getIdUser());

            Warehouse warehouse;
            if (!Objects.equals(dto.getIdWarehouse(), oldLease.getIdWarehouse())) {
                warehouse = this.warehouseService.checkWarehouseExistence(dto.getIdWarehouse());
            } else {
                warehouse = warehouseValid(dto.getIdWarehouse());
            }

            compareExtraServicesOfLease(oldLease.getExtraServices(), dto.getExtraServices(), id);

            Lease entity = new Lease();
            BeanUtils.copyProperties(dto, entity, "id", "total");

            if( !dto.getExtraServices().isEmpty() ) {
                List<ServiceDomain> extraServicesDomain = this.serviceDomainService.findServicesByName(dto.getExtraServices());
                Double total = calculateTotal(warehouse.getPrice(), extraServicesDomain );
                entity.setTotal(total);
            }else{
                entity.setTotal(warehouse.getPrice());
            }

            this.repository.save(entity);

            return this.findOne(id);


        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws GenericException, BadRequestException {
        try {

            Optional<Lease> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The lease with the id " + id + " does not exist");
            }

            List<ServiceDomain> serviceDomains = this.serviceLeaseService.findServicesByIdLease(id);
            if (!serviceDomains.isEmpty()) {

                for (ServiceDomain serviceDomain : serviceDomains) {

                    ServiceLeaseId serviceLeaseId = new ServiceLeaseId();
                    serviceLeaseId.setId_service(serviceDomain.getId());
                    serviceLeaseId.setId_lease(id);

                    this.serviceLeaseService.delete(serviceLeaseId);

                }

            }

            this.repository.delete(valid.get());

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<LeaseDTO> findAll(String search, Pageable page) throws NoContentException, GenericException {
        try {

            Page<Lease> response;
            if (search != null) {
                response = this.repository.findAllByIdUserContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }

            if (response.isEmpty()) {
                throw new NoContentException("No records found");
            }

            List<LeaseDTO> leaseWithServices = new ArrayList<>();
            for (Lease lease : response) {
                leaseWithServices.add(this.findOne(lease.getId()));
            }

            return new PageImpl<>(leaseWithServices, response.getPageable(), response.getTotalElements());

        } catch (NoContentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    /**
     * Check if the warehouse meets all the requirements
     * @param id Code id of the warehouse to verify
     * @return valid warehouse
     * @throws GenericException    exception general
     * @throws BadRequestException exception if the warehouse is not valid
     */
    private Warehouse warehouseValid(Integer id) throws GenericException, BadRequestException {
        try {

            Optional<Warehouse> availableWarehouse = this.warehouseRepository.findById(id);

            if (availableWarehouse.isEmpty()) {
                throw new BadRequestException("The warehouse does not exist");
            }

            if (!availableWarehouse.get().getAvailable()) {
                throw new BadRequestException("The warehouse is not available.");
            }

            return availableWarehouse.get();

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    /**
     * Check if the user is valid
     *
     * @param id Code id of the user to verify
     * @throws GenericException    exception general
     * @throws BadRequestException exception to invalid user
     */
    private void userValid(Integer id) throws GenericException, BadRequestException {
        try {

            Optional<UserDomain> valid = this.userRepository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The user does not exist");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    private Double calculateTotal(Double priceWarehouse, List<ServiceDomain> extraServices) {

        Double total = priceWarehouse;

        if (!extraServices.isEmpty()) {
            for (ServiceDomain service : extraServices) {

                total += service.getPrice();

            }
        }

        return total;

    }

    private List<String> findExtraServiceNamesByLease(Integer idLease) throws GenericException{
        try {

            List<ServiceDomain> extraServices = this.serviceLeaseService.findServicesByIdLease(idLease);

            return extraServices.stream().map(
                    ServiceDomain::getName
            ).toList();

        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    private void compareExtraServicesOfLease(List<String> oldServices, List<String> newServices, Integer idLease) throws GenericException, BadRequestException{
        try {

            //Transform name Lists to ID Lists

            Map<String, List<Integer>> lists = this.serviceDomainService.compareServiceLists(oldServices, newServices);

            List<Integer> oldIds = lists.get("First list");
            List<Integer> newIds = lists.get("Second list");

            if (!oldIds.isEmpty()) {
                this.serviceLeaseService.deleteAllServicesByLease(oldIds, idLease);
            }
            if (!newIds.isEmpty()) {
                this.serviceLeaseService.saveAllServicesByLease(newIds, idLease);
            }

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

}
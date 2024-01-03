package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.ServiceDomain;
import com.diegomorales.warehouse.domain.ServiceLease;
import com.diegomorales.warehouse.domain.ServiceWarehouse;
import com.diegomorales.warehouse.dto.ServiceDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.repository.ServiceLeaseRepository;
import com.diegomorales.warehouse.repository.ServiceRepository;
import com.diegomorales.warehouse.repository.ServiceWarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class ServiceDomainService {

    private ServiceRepository serviceDomainRepository;
    private ServiceWarehouseRepository serviceWarehouseRepository;
    private ServiceLeaseRepository serviceLeaseRepository;

    public ServiceDomain save(ServiceDTO dto) throws GenericException, BadRequestException, DataIntegrityViolationException{
        try {
            Optional<ServiceDomain> valid = this.serviceDomainRepository.findFirstByNameIgnoreCase(dto.getName());
            if (valid.isPresent()) {
                throw new BadRequestException("The name of the service is already in use");
            }

            var entity = new ServiceDomain();
            BeanUtils.copyProperties(dto, entity);
            entity.setId(null);

            return this.serviceDomainRepository.save(entity);

        }catch (BadRequestException e){
            throw e;
        }catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Violation error in foreign key");
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public ServiceDTO findOne(Integer id) throws BadRequestException, GenericException{
        try {

            Optional<ServiceDomain> valid = this.serviceDomainRepository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The service with id : " + id + " does not exist");
            }
            var dto = new ServiceDTO();
            BeanUtils.copyProperties(valid.get(), dto);
            return dto;
        }catch (BadRequestException e){
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public ServiceDomain update(Integer id, ServiceDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<ServiceDomain> valid = this.serviceDomainRepository.findById(id);
            if(valid.isEmpty()){
                throw new BadRequestException("The service does not exists");
            }

            Optional<ServiceDomain> validName = this.serviceDomainRepository.findFirstByNameIgnoreCase(dto.getName());
            if (validName.isPresent() && !Objects.equals(validName.get().getId(), id)) {
                throw new BadRequestException("The name of the new service is already in use");
            }

            BeanUtils.copyProperties(dto, valid.get(), "id");
            return this.serviceDomainRepository.save(valid.get());

        }catch (BadRequestException e){
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void disable(Integer id) throws BadRequestException, GenericException{
        try {
            Optional<ServiceDomain> valid = this.serviceDomainRepository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The service does not exits");
            }

            //Check if the service is in use
            checkServiceInUse(id);

            this.serviceDomainRepository.delete(valid.get());

        }catch (BadRequestException e){
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<ServiceDomain> findAll(String search, Pageable page) throws NoContentException, GenericException{
        try {
            Page<ServiceDomain> response;

            if(search != null && search.isEmpty()){
                response = this.serviceDomainRepository.findAllByNameNotContainsIgnoreCase(search, page);
            }else {
                response = this.serviceDomainRepository.findAll(page);
            }
            if(response.isEmpty()){
                throw new NoContentException("No services found");
            }
            return response;

        }catch (NoContentException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<ServiceDomain> findServicesByName(List<String> servicesNames) throws BadRequestException{

        List<ServiceDomain> services = new ArrayList<>();

        for(String serviceName : servicesNames){
            if (serviceName != null && !serviceName.isEmpty()) {

                Optional<ServiceDomain> valid = this.serviceDomainRepository.findFirstByNameIgnoreCase(serviceName);
                if(valid.isEmpty()){
                    throw new BadRequestException("The service " + serviceName + " does not exist");
                }

                services.add( valid.get() );
            }

        }

        return services;

    }

    public List<Integer> findServicesIDsByName(List<String> services) throws BadRequestException {
        List<ServiceDomain> domains = this.findServicesByName(services);
        return domains.stream().map(
                ServiceDomain::getId
        ).toList();
    }

    public Map<String, List<Integer>> compareServiceLists(List<String> firstList, List<String> secondList) throws BadRequestException {

        List<Integer> firstListByIDs = findServicesIDsByName(firstList);
        List<Integer> secondListByIDs = findServicesIDsByName(secondList);

        ArrayList<Integer> firstArrayList = new ArrayList<>(firstListByIDs);
        ArrayList<Integer> secondArrayList = new ArrayList<>(secondListByIDs);

        for (Integer id : secondListByIDs) {
            var index = firstListByIDs.indexOf(id);
            if (index != -1) {
                firstArrayList.remove(id);
                secondArrayList.remove(id);
            }
        }

        List<Integer> returnListOne = firstArrayList.stream().toList();
        List<Integer> returnListSecond = secondArrayList.stream().toList();

        Map<String, List<Integer>> map = new HashMap<>();
        map.put("First list", returnListOne);
        map.put("Second list", returnListSecond);

        return map;

    }

    public void checkServiceInUse(Integer idService) throws BadRequestException{

        Optional<ServiceWarehouse> serviceWarehouse = this.serviceWarehouseRepository.findFirstByServiceId(idService);
        if(serviceWarehouse.isPresent()){
            throw new BadRequestException("The service is in use by a warehouse");
        }

        Optional<ServiceLease> serviceLease = this.serviceLeaseRepository.findFirstByIdService(idService);
        if (serviceLease.isPresent()) {
            throw new BadRequestException("The service is in use by a lease");
        }


    }


}

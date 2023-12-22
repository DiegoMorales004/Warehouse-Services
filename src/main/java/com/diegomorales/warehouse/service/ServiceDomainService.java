package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.ServiceDomain;
import com.diegomorales.warehouse.dto.ServiceDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.repository.ServiceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ServiceDomainService {

    private ServiceRepository serviceDomainRepository;

    public ServiceDomain save(ServiceDTO dto) throws GenericException, BadRequestException, DataIntegrityViolationException{
        try {
            Optional<ServiceDomain> valid = this.serviceDomainRepository.findFirstByNameContainsIgnoreCase(dto.getName());
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
                throw new BadRequestException("The service does not exist");
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

            Optional<ServiceDomain> validName = this.serviceDomainRepository.findFirstByNameContainsIgnoreCase(dto.getName());
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

    public void delete(Integer id) throws BadRequestException, GenericException{
        try {
            Optional<ServiceDomain> valid = this.serviceDomainRepository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The service does not exits");
            }

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

    public List<Integer> findServicesByName(List<String> servicesNames) throws BadRequestException{

        List<Integer> ids = new ArrayList<>();

        for(String serviceName : servicesNames){
            Optional<ServiceDomain> valid = this.serviceDomainRepository.findFirstByNameContainsIgnoreCase(serviceName);
            if(valid.isEmpty()){
                throw new BadRequestException("The service " + serviceName + " does not exist");
            }

            ids.add(valid.get().getId());

        }

        return ids;

    }


}

package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.ServiceDomain;
import com.diegomorales.warehouse.dto.ServiceDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.repository.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ServiceDomainService {

    @Autowired
    private ServiceRepository serviceDomainRepository;

    public ServiceDomain save(ServiceDTO dto) throws GenericException, BadRequestException{
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

}

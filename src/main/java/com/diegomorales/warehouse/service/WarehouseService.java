package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.Branch;
import com.diegomorales.warehouse.domain.UserDomain;
import com.diegomorales.warehouse.domain.Warehouse;
import com.diegomorales.warehouse.dto.WarehouseDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.repository.BranchRepository;
import com.diegomorales.warehouse.repository.UserRepository;
import com.diegomorales.warehouse.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class WarehouseService {

    private WarehouseRepository repository;
    private BranchRepository branchRepository;
    private UserRepository userRepository;
    private ServiceWarehouseService serviceWarehouseService;

    public Warehouse save(WarehouseDTO dto) throws GenericException, BadRequestException{
        try {

            Optional<Warehouse> valid = this.repository.findFirstByCodeContainsIgnoreCase(dto.getCode());
            if(valid.isPresent()){
                throw new BadRequestException("The code is already in use");
            }

            Optional<Branch> validBranch = this.branchRepository.findById(dto.getId_branch());
            if(validBranch.isEmpty()){
                throw new BadRequestException("The branch does not exit");
            }

            checkUserExistence(dto.getId_user());

            var entity = new Warehouse();
            BeanUtils.copyProperties(dto, entity);

            var saved = this.repository.save(entity);

            if (!dto.getExtraServices().isEmpty()) {
                serviceWarehouseService.saveAllById(dto.getExtraServices(), saved.getId());
            }

            return saved;

        }catch (BadRequestException e){
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    private void checkUserExistence(Integer id) throws BadRequestException{
        if(id != null){
            Optional<UserDomain> validUser = this.userRepository.findById(id);
            if(validUser.isEmpty()){
                throw new BadRequestException("The user does not exit");
            }
        }
    }

}

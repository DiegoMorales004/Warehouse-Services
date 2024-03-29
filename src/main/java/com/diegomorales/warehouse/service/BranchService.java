package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.Branch;
import com.diegomorales.warehouse.domain.Warehouse;
import com.diegomorales.warehouse.dto.BranchDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.repository.BranchRepository;
import com.diegomorales.warehouse.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BranchService {

    private BranchRepository repository;
    private WarehouseRepository warehouseRepository;

    private WarehouseService warehouseService;

    public Branch save(BranchDTO dto) throws GenericException, BadRequestException {
        try {

            Optional<Branch> validName = this.repository.findFirstByNameIgnoreCase(dto.getName());
            if (validName.isPresent()) {
                throw new BadRequestException("The name is already in use");
            }

            Optional<Branch> validCode = this.repository.findFirstByCodeIgnoreCase(dto.getCode());
            if (validCode.isPresent()) {
                throw new BadRequestException("The code is already in use");
            }

            var entity = new Branch();
            BeanUtils.copyProperties(dto, entity);
            entity.setId(null);

            return this.repository.save(entity);

        }catch (BadRequestException e){
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public BranchDTO findOne(Integer id) throws GenericException, BadRequestException{
        try {

            Optional<Branch> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The branch does not exist");
            }

            var dto = new BranchDTO();
            BeanUtils.copyProperties(valid.get(), dto);

            return dto;

        }catch (BadRequestException e){
            throw  e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Branch update(Integer id, BranchDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Branch> valid = this.repository.findById(id);
            if(valid.isEmpty()) {
                throw new BadRequestException("The branch with this id does not exit");
            }

            Optional<Branch> validName = this.repository.findFirstByNameIgnoreCase(dto.getName());
            if (validName.isPresent() && !Objects.equals(validName.get().getId(), id)) {
                throw new BadRequestException("The name is already in use");
            }

            Optional<Branch> validCode = this.repository.findFirstByCodeIgnoreCase(dto.getCode());
            if (validCode.isPresent() && !Objects.equals(validCode.get().getId(), id) ) {
                throw new BadRequestException("The code is already in use");
            }

            BeanUtils.copyProperties(dto, valid.get(), "id");

            return this.repository.save(valid.get());

        }catch (BadRequestException e){
            throw  e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Branch disable(Integer id) throws GenericException, BadRequestException{
        try {

            Optional<Branch> valid = this.repository.findById(id);
            if(valid.isEmpty()) {
                throw new BadRequestException("The branch with id " + id + " does not exist");
            }

            valid.get().setActive(false);

            List<Warehouse> warehouses = this.warehouseRepository.findAllByIdBranch(id);
            for (Warehouse warehouse : warehouses) {
                this.warehouseService.disableWarehouse(warehouse);
            }

            return this.repository.save(valid.get());

        }catch (BadRequestException e){
            throw  e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Branch> findAll(String search, Pageable page) throws NoContentException, GenericException{
        try {

            Page<Branch> response;
            if(search != null && search.isEmpty()){
                response = this.repository.findAllByNameContainsIgnoreCase(search, page);
            }else {
                response = this.repository.findAll(page);
            }
            if(response.isEmpty()){
                throw new NoContentException("No branches found");
            }

            return response;

        }catch (NoContentException e){
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

}

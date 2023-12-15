package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.Role;
import com.diegomorales.warehouse.dto.RoleDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository repository;

    public Role save(RoleDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Role> valid = this.repository.findFirsByNameContainsIgnoreCase(dto.getName());
            if (valid.isPresent()) {
                throw new BadRequestException("The role whit this name already exists.");
            }
            var entity = new Role();
            BeanUtils.copyProperties(dto, entity);
            entity.setId(null);
            return this.repository.save(entity);
        }catch (BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public RoleDTO findOne(Integer id) throws GenericException, BadRequestException {
        try {
            Optional<Role> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The role does not exists.");
            }
            var dto = new RoleDTO();
            BeanUtils.copyProperties(valid.get(), dto);
            return dto;
        }catch (BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Role update(Integer id, RoleDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Role> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The role does not exists");
            }

            Optional<Role> nameExits = this.repository.findFirsByNameContainsIgnoreCase(dto.getName());
            if(nameExits.isPresent() && !Objects.equals(nameExits.get().getId(), id)){
                throw new BadRequestException("The role already exists");
            }

            BeanUtils.copyProperties(dto, valid.get(), "id");

            return this.repository.save(valid.get());

        }catch(BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws GenericException, BadRequestException, DataIntegrityViolationException {
        try {
            Optional<Role> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The role does not exists");
            }
            this.repository.deleteById(id);
        }catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Violation error in foreign key");
        }catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Role> findAll(String search, Pageable page) throws GenericException, NoContentException{
        try {
            Page<Role> response;
            if (search != null && search.isEmpty()) {
                response = this.repository.findAllByNameContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }
            if (response.isEmpty()) {
                throw new NoContentException("No roles found");
            }
            return response;
        }catch (NoContentException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    /**
     * Search for non-existent roles in the database.
     * @param list List of roles we want to verify.
     * @return List of roles that do not exist.
     */
    public List<String> incorrectRoles(List<String> list){
            List<String> notFound = new ArrayList<>(Collections.emptyList());
            list.forEach(
                    _role -> {
                        Optional<Role> valid = this.repository.findFirsByNameContainsIgnoreCase(_role);
                        if(valid.isEmpty()) notFound.add(_role);
                    }
            );

            return notFound;
    }

}

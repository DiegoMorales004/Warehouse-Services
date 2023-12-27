package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.UserDomain;
import com.diegomorales.warehouse.dto.UserDTO;
import com.diegomorales.warehouse.exception.*;
import com.diegomorales.warehouse.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    public UserDomain save(UserDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<UserDomain> validEmail = this.repository.findFirstByEmailContainsIgnoreCase(dto.getEmail());
            if (validEmail.isPresent()) {
                throw new BadRequestException("The user with this email already exists.");
            }

            VerifyUserName_Roles(dto);

            return savingProcess(dto);

        }catch (BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public UserDomain saveWithDefaultRole(UserDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<UserDomain> valid = this.repository.findFirstByEmailContainsIgnoreCase(dto.getEmail());
            if (valid.isPresent()) {
                throw new BadRequestException("The user with this email already exists");
            }

            Optional<UserDomain> validUserName = this.repository.findFirstByUsernameContainsIgnoreCase(dto.getUsername());
            if (validUserName.isPresent()) {
                throw new BadRequestException("The user with this userName already exists.");
            }

            dto.setRoles(List.of("USER"));

            return savingProcess(dto);


        }catch (BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public UserDTO findOne(Integer id) throws GenericException, BadRequestException{
        try {

            Optional<UserDomain> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The user does not exists");
            }

            var dto = new UserDTO();
            BeanUtils.copyProperties(valid.get(), dto);

            List<String> roles = this.userRoleService.findAllRolesByUser(id);

            dto.setRoles(roles);

            return dto;
        }catch (BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void update(Integer id, UserDTO dto) throws GenericException, BadRequestException{
        try {

            Optional<UserDomain> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The user does not exists");
            }

            VerifyUserName_Roles(dto);

            BeanUtils.copyProperties(dto, valid.get(), "id");
            valid.get().setPassword(encryptPassword(valid.get().getPassword()));

            roleComparison(id, dto.getRoles());
            this.repository.save(valid.get());


        }catch (BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws GenericException, BadRequestException {
        try {
            Optional<UserDomain> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("The user does not exists");
            }

            this.userRoleService.deleteByUser(id);
            this.repository.delete(valid.get());

        }catch (BadRequestException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<UserDTO> findAll(String search, Pageable page) throws GenericException, NoContentException{
        try {
            Page<UserDomain> response;
            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByUsernameContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }
            if (response.isEmpty()) {
                throw new NoContentException("No records found");
            }

            List<UserDTO> usersWithRoles = new ArrayList<>();

            for (UserDomain user : response) {
                usersWithRoles.add( this.findOne(user.getId() ) );
            }

            return new PageImpl<>(usersWithRoles, response.getPageable(), response.getTotalElements());

        }catch (NoContentException e){
            throw e;
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    private void VerifyUserName_Roles(UserDTO dto) throws BadRequestException {
        Optional<UserDomain> validUserName = this.repository.findFirstByUsernameContainsIgnoreCase(dto.getUsername());
        if (validUserName.isPresent()) {
            throw new BadRequestException("The user with this userName already exists.");
        }

        List<String> nonExistentRoles = this.roleService.incorrectRoles(dto.getRoles());
        if (!nonExistentRoles.isEmpty()) {
            throw new BadRequestException("The role(s): " + nonExistentRoles + ", do not exist");
        }
    }

    /**
     * Repeated process in the saveWithDefaultRole and save function
     * @param dto DTO to save
     */
    private UserDomain savingProcess(UserDTO dto){
        var entity = new UserDomain();
        BeanUtils.copyProperties(dto, entity);
        entity.setPassword(encryptPassword(entity.getPassword()));
        entity.setId(null);
        var userSaved = this.repository.save(entity);

        dto.getRoles().forEach(
                _role -> {
                    try {
                        this.userRoleService.save(userSaved.getId(), _role);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return userSaved;
    }

    /**
     * Encrypt the password with BCrypt
     * @param password String to encode
     * @return Coded String
     */
    public String encryptPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Method for Matching Incoming Role Changes
     * @param id id of the user
     * @param newRoles List of the Roles in the DTO
     * @throws Exception Exception
     */
    public void roleComparison(Integer id, List<String> newRoles) throws Exception{
        try{
            //List of saved roles
            List<String> oldRoles = this.userRoleService.findAllRolesByUser(id);

            Iterator<String> iterator = newRoles.iterator();
            while (iterator.hasNext()) {
                String _role = iterator.next();
                var index = oldRoles.indexOf(_role);
                if (index != -1) {
                    iterator.remove();
                    oldRoles.remove(_role);
                }
            }

            if(!oldRoles.isEmpty()){
                this.userRoleService.deleteAllRolesByName(id, oldRoles);
            }
            if(!newRoles.isEmpty()){
                this.userRoleService.addAllRolesByName(id, newRoles);
            }
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }

    }

}

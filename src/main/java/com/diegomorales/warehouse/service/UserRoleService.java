package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.Role;
import com.diegomorales.warehouse.domain.UserRole;
import com.diegomorales.warehouse.domain.UserRoleId;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.repository.RoleRepository;
import com.diegomorales.warehouse.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public void save(Integer id_user, String nameRole) throws GenericException, BadRequestException {
        try {

            Optional<Role> role = this.roleRepository.findFirsByNameContainsIgnoreCase(nameRole);
            if (role.isEmpty()) {
                return;
            }

            var entity = new UserRole();
            entity.setUserRoleId(new UserRoleId(id_user, role.get().getId()));
            this.userRoleRepository.save(entity);

        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    /**
     * Method used when a user is deleted and it is necessary to delete all their roles
     * @param id_user ID of the user from whom all roles will be deleted
     */
    public void deleteByUser(Integer id_user) throws GenericException{
        try{
            List<UserRole> findRoles = this.userRoleRepository.findByUserId(id_user);
            this.userRoleRepository.deleteAll(findRoles);
        }catch (Exception e){
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    /**
     * Find all roles by user, with the id of the user
     * @param id ID of the user for whom you want the roles
     * @return Sting listing of user roles
     */

    public List<String> findAllRolesByUser(Integer id) throws GenericException {
        try {
            List<UserRole> findRoles = this.userRoleRepository.findByUserId(id);

            //Find all roles of the user
            List<Integer> rolesId = new ArrayList<>();
            for (UserRole findRole : findRoles) {
                rolesId.add(findRole.getUserRoleId().getId_rol());
            }

            //Get name of the role by id
            List<String> roles = new ArrayList<>();
            rolesId.forEach(
                    idRole -> {
                        Optional<Role> role = this.roleRepository.findById(idRole);
                        role.ifPresent(value -> roles.add(value.getName()));
                    }
            );

            return roles;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }

    }

    /**
     * Method to delete records from the auxiliary table when roles have changed
     * @param idUser evaluated user id
     * @param roles list of roles to delete (name)
     */
    public void deleteAllRolesByName(Integer idUser, List<String> roles) {

        //List of couples to eliminate
        List<UserRole> userRoles = new ArrayList<>();

        roles.forEach(
                role -> {
                    var completeRole = this.roleRepository.findFirsByNameContainsIgnoreCase(role).orElse(new Role());
                    userRoles.add(new UserRole(new UserRoleId(idUser, completeRole.getId())));
                }
        );

        this.userRoleRepository.deleteAll(userRoles);

    }

    /**
     *
     * @param idUser evaluated user id
     * @param roles list of roles to add (name)
     */
    public void addAllRolesByName(Integer idUser, List<String> roles){
        //List of couples to add
        List<UserRole> userRoles = new ArrayList<>();

        roles.forEach(
                role -> {
                    var completeRole = this.roleRepository.findFirsByNameContainsIgnoreCase(role).orElse(new Role());
                    userRoles.add(new UserRole(new UserRoleId(idUser, completeRole.getId())));
                }
        );

        this.userRoleRepository.saveAll(userRoles);
    }


}

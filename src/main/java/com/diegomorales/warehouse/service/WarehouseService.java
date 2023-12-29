package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.*;
import com.diegomorales.warehouse.dto.WarehouseDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.exception.NoContentException;
import com.diegomorales.warehouse.repository.*;
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
public class WarehouseService {

    private WarehouseRepository repository;
    private ServiceRepository serviceRepository;
    private BranchRepository branchRepository;
    private ServiceWarehouseRepository serviceWarehouseRepository;

    private ServiceWarehouseService serviceWarehouseService;
    private ServiceDomainService serviceDomainService;

    public Warehouse save(WarehouseDTO dto) throws GenericException, BadRequestException {
        try {

            Optional<Warehouse> valid = this.repository.findFirstByCodeContainsIgnoreCase(dto.getCode());
            if (valid.isPresent()) {
                throw new BadRequestException("The code is already in use");
            }

            Optional<Branch> validBranch = this.branchRepository.findById(dto.getId_branch());
            if (validBranch.isEmpty()) {
                throw new BadRequestException("The branch does not exit");
            }

            var entity = new Warehouse();
            BeanUtils.copyProperties(dto, entity);

            var saved = this.repository.save(entity);

            if (!dto.getExtraServices().isEmpty()) {
                List<Integer> idServices = this.serviceWarehouseService.findAllServicesById(dto.getExtraServices());
                this.serviceWarehouseService.saveAllById(idServices, saved.getId());
            }

            return saved;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public WarehouseDTO findOne(Integer id) throws GenericException, BadRequestException {
        try {

            Warehouse valid = checkWarehouseExistence(id);

            List<ServiceWarehouse> extraServices = this.serviceWarehouseRepository.findAllByWarehouseId(id);

            List<Integer> extraServicesIds = extraServices.stream().map(
                    ExSer ->
                            ExSer.getServiceWarehouseId().getId_service()
            ).toList();

            List<String> extraServicesNames = new ArrayList<>();

            for (Integer idService : extraServicesIds) {
                Optional<ServiceDomain> validService = this.serviceRepository.findById(idService);

                validService.ifPresent(serviceDomain -> extraServicesNames.add(serviceDomain.getName()));
            }

            var dto = new WarehouseDTO();
            BeanUtils.copyProperties(valid, dto);

            dto.setExtraServices(extraServicesNames);

            return dto;


        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public WarehouseDTO update(Integer id, WarehouseDTO dto) throws GenericException, BadRequestException {
        try {

            Warehouse valid = checkWarehouseExistence(id);

            Optional<Warehouse> validWarehouse = this.repository.findFirstByCodeContainsIgnoreCase(dto.getCode());
            if (validWarehouse.isPresent() && !Objects.equals(validWarehouse.get().getId(), id)) {
                throw new BadRequestException("The code is already in use");
            }

            Optional<Branch> validBranch = this.branchRepository.findById(dto.getId_branch());
            if (validBranch.isEmpty()) {
                throw new BadRequestException("The branch does not exit");
            }

            WarehouseDTO oldWarehouse = this.findOne(id);

            compareServicesOfWarehouse(oldWarehouse.getExtraServices(), dto.getExtraServices(), id);

            BeanUtils.copyProperties(dto, valid, "id");

            this.repository.save(valid);

            return this.findOne(id);


        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws BadRequestException, GenericException {
        try {

            Warehouse valid = checkWarehouseExistence(id);

            this.repository.delete(valid);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<WarehouseDTO> findAll(String search, Pageable page) throws NoContentException, GenericException {
        try {

            Page<Warehouse> response;
            if(search != null && search.isEmpty()){
                response = this.repository.findAllByCodeContainsIgnoreCase(search, page);
            }else{
                response = this.repository.findAll(page);
            }

            if(response.isEmpty()) {
                throw new NoContentException("No warehouses found");
            }

            List<WarehouseDTO> listWarehouseDTO = new ArrayList<>();

            for (Warehouse warehouse : response) {
                listWarehouseDTO.add( this.findOne( warehouse.getId() ) );
            }

            return new PageImpl<>(listWarehouseDTO, response.getPageable(), response.getTotalElements());


        }catch (NoContentException e){
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    /**
     * @param id Code id of the warehouse to check
     * @return Warehouse found
     * @throws BadRequestException Not found exception
     */
    public Warehouse checkWarehouseExistence(Integer id) throws BadRequestException {

        Optional<Warehouse> valid = this.repository.findById(id);
        if (valid.isEmpty()) {
            throw new BadRequestException("The warehouse does not exist");
        }

        return valid.get();
    }

    /**
     * Compare the two lists to find out which relationships to delete or create.
     *
     * @param oldServices List (String) of the old services
     * @param newServices List (String) of the new services
     * @param idWarehouse id of the warehouse on which everything will be worked
     * @throws GenericException    General error
     * @throws BadRequestException Error when transforming lists into integers
     */
    private void compareServicesOfWarehouse(List<String> oldServices, List<String> newServices, Integer idWarehouse) throws GenericException, BadRequestException {
        try {

            //Transform name Lists to ID Lists

            Map<String, List<Integer>> lists = this.serviceDomainService.compareServiceLists(oldServices, newServices);

            List<Integer> oldIds = lists.get("First list");
            List<Integer> newIds = lists.get("Second list");

            if (!oldIds.isEmpty()) {
                this.serviceWarehouseService.deleteAllServicesByWarehouse(oldIds, idWarehouse);
            }
            if (!newIds.isEmpty()) {
                this.serviceWarehouseService.saveAllById(newIds, idWarehouse);
            }


        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Processing error", e);
            throw new GenericException("Error processing request");
        }
    }

    public void disableWarehouse(Warehouse warehouse) {
        warehouse.setAvailable(false);
        this.repository.save(warehouse);
    }

    public void activateWarehouse(Warehouse warehouse) {
        warehouse.setAvailable(true);
        this.repository.save(warehouse);
    }

}

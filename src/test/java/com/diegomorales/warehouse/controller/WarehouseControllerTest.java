package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.Branch;
import com.diegomorales.warehouse.domain.Warehouse;
import com.diegomorales.warehouse.dto.WarehouseDTO;
import com.diegomorales.warehouse.repository.BranchRepository;
import com.diegomorales.warehouse.repository.LeaseRepository;
import com.diegomorales.warehouse.repository.ServiceWarehouseRepository;
import com.diegomorales.warehouse.repository.WarehouseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class WarehouseControllerTest {

    private final static String BASE_URL = "/api/warehouse";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WarehouseRepository warehouseRepository;
    @MockBean
    BranchRepository branchRepository;
    @MockBean
    ServiceWarehouseRepository serviceWarehouseRepository;
    @MockBean
    LeaseRepository leaseRepository;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception{

        when( warehouseRepository.findFirstByCodeIgnoreCase( any() ) ).thenReturn( Optional.empty() );
        when( warehouseRepository.save( any() ) ).thenReturn( warehouseDomain() );
        when( branchRepository.findById( any() ) ).thenReturn( Optional.of( branchDomain() ) );

        mvc.perform(
                post(BASE_URL)
                        .content( objectMapper.writeValueAsString( warehouseDTO() ) )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOne() throws Exception{

        when( warehouseRepository.findById( any() ) ).thenReturn( Optional.of( warehouseDomain() ) );
        when( serviceWarehouseRepository.findAllByWarehouseId( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                get(BASE_URL + "/1")
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void update() throws Exception{

        when( warehouseRepository.findById( any() ) ).thenReturn( Optional.of( warehouseDomain() ) );
        when( warehouseRepository.findFirstByCodeIgnoreCase( any() ) ).thenReturn( Optional.empty() );
        when( branchRepository.findById( any() ) ).thenReturn( Optional.of( branchDomain() ) );

        //call findOne
        when( serviceWarehouseRepository.findAllByWarehouseId( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                put(BASE_URL + "/1")
                        .content( objectMapper.writeValueAsString( warehouseDTO() ) )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void disable() throws  Exception{

        when( warehouseRepository.findById( any() ) ).thenReturn( Optional.of( warehouseDomain() ) );
        when( leaseRepository.findFirstByIdWarehouse( any() ) ).thenReturn( Optional.empty() );

        mvc.perform(
                put(BASE_URL + "/disable/1")
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAll() throws Exception{

        Page<Warehouse> page = new PageImpl<>( List.of( warehouseDomain() ) );

        when( warehouseRepository.findAll( any( PageRequest.class) ) ).thenReturn( page );

        //call findOne
        when( warehouseRepository.findById( any() ) ).thenReturn( Optional.of( warehouseDomain() ) );
        when( serviceWarehouseRepository.findAllByWarehouseId( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    private Warehouse warehouseDomain() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1);
        warehouse.setAvailable(true);
        warehouse.setCode("123test");
        warehouse.setDescription("test");
        warehouse.setIdBranch(1);
        warehouse.setSize("100m");
        warehouse.setPrice(100.0);
        return warehouse;
    }

    private WarehouseDTO warehouseDTO(){
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(1);
        warehouseDTO.setAvailable(true);
        warehouseDTO.setCode("123test");
        warehouseDTO.setDescription("test");
        warehouseDTO.setIdBranch(1);
        warehouseDTO.setSize("100m");
        warehouseDTO.setPrice(100.0);
        warehouseDTO.setExtraServices(Collections.emptyList());
        return warehouseDTO;
    }

    private Branch branchDomain() {
        Branch branch = new Branch();
        branch.setId(1);
        branch.setActive(true);
        branch.setName("Test");
        branch.setCode("123Test");
        branch.setParking(true);
        branch.setLocation("Test");
        return branch;
    }

}
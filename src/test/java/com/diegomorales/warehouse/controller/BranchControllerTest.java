package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.Branch;
import com.diegomorales.warehouse.dto.BranchDTO;
import com.diegomorales.warehouse.repository.BranchRepository;
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
class BranchControllerTest {

    private final String BASE_URL = "/api/branch";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BranchRepository branchRepository;
    @MockBean
    WarehouseRepository warehouseRepository;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception{

        when( branchRepository.findFirstByNameIgnoreCase( any() ) ).thenReturn(Optional.empty() );
        when( branchRepository.findFirstByCodeIgnoreCase( any() ) ).thenReturn( Optional.empty() );
        when( branchRepository.save( any() ) ).thenReturn( branchDomain() );

        mvc.perform(
                post(BASE_URL)
                        .content( objectMapper.writeValueAsString( branchDTO() ) )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOne() throws  Exception{

        when( branchRepository.findById( any() ) ).thenReturn( Optional.of( branchDomain() ) );

        mvc.perform(
                get(BASE_URL + "/1")
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void update() throws Exception{

        when( branchRepository.findById( any() ) ).thenReturn( Optional.of( branchDomain() ) );
        when( branchRepository.findFirstByNameIgnoreCase( any() ) ).thenReturn( Optional.empty() );
        when( branchRepository.findFirstByCodeIgnoreCase( any() ) ).thenReturn( Optional.empty() );

        mvc.perform(
                put(BASE_URL + "/1")
                        .content( objectMapper.writeValueAsString( branchDTO() ) )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void disable() throws Exception{

        when( branchRepository.findById( any() ) ).thenReturn( Optional.of( branchDomain() ) );
        when( warehouseRepository.findAllByIdBranch( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                put(BASE_URL + "/disable/1")
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAll() throws Exception{

        Page<Branch> page = new PageImpl<>( List.of( branchDomain() ) );
        when( branchRepository.findAll( any(PageRequest.class) ) ).thenReturn( page );

        mvc.perform(
                get(BASE_URL)
        ).andExpect(status().isOk());

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

    private BranchDTO branchDTO() {
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setId(1);
        branchDTO.setActive(true);
        branchDTO.setName("Test");
        branchDTO.setCode("123Test");
        branchDTO.setParking(true);
        branchDTO.setLocation("Test");
        return branchDTO;
    }

}
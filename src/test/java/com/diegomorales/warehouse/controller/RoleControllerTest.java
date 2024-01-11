package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.Role;
import com.diegomorales.warehouse.dto.RoleDTO;
import com.diegomorales.warehouse.repository.RoleRepository;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoleControllerTest {

    private final String BASE_URL = "/api/role";
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private RoleRepository roleRepository;

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void save() throws Exception{

        when(roleRepository.findFirsByNameContainsIgnoreCase(any())).thenReturn(Optional.empty());
        when(roleRepository.save(any())).thenReturn(roleDomain());

        mvc.perform(
                post(BASE_URL)
                        .content(objectMapper.writeValueAsString(roleDTO()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void findOne() throws Exception {
        when(roleRepository.findById(any())).thenReturn(Optional.of(roleDomain()));

        mvc.perform(
                get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void update() throws Exception{
        when(roleRepository.findById(any())).thenReturn(Optional.of(roleDomain()));
        when(roleRepository.findFirsByNameContainsIgnoreCase(any())).thenReturn(Optional.empty());

        mvc.perform(
                put(BASE_URL + "/1")
                        .content(objectMapper.writeValueAsString(roleDTO()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void deleteRole() throws Exception{
        when(roleRepository.findById(any())).thenReturn(Optional.of(roleDomain()));

        mvc.perform(
                delete(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void findAll() throws Exception{

        Page<Role> page = new PageImpl<>(List.of(roleDomain()));

        when(roleRepository.findAll( any( PageRequest.class) ) ).thenReturn(page);

        mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    private RoleDTO roleDTO() {
        RoleDTO dto = new RoleDTO();
        dto.setId(1);
        dto.setDescription("Role");
        dto.setName("Role");
        return dto;
    }

    private Role roleDomain(){
        Role domain = new Role();
        domain.setId(1);
        domain.setDescription("Role");
        domain.setName("Role");
        return domain;
    }

}
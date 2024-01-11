package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.ServiceDomain;
import com.diegomorales.warehouse.dto.ServiceDTO;
import com.diegomorales.warehouse.repository.ServiceLeaseRepository;
import com.diegomorales.warehouse.repository.ServiceRepository;
import com.diegomorales.warehouse.repository.ServiceWarehouseRepository;
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
class ServiceControllerTest {

    private final String BASE_URL = "/api/service";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ServiceRepository serviceRepository;
    @MockBean
    private ServiceLeaseRepository serviceLeaseRepository;
    @MockBean
    private ServiceWarehouseRepository serviceWarehouseRepository;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception{

        when( serviceRepository.findFirstByNameIgnoreCase( any() ) ).thenReturn(Optional.empty() );
        when( serviceRepository.save( any() ) ).thenReturn( serviceDomain() );

        mvc.perform(
                post(BASE_URL)
                        .content(objectMapper.writeValueAsString( serviceDTO() ))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOne() throws Exception{

        when( serviceRepository.findById( any() ) ).thenReturn( Optional.of( serviceDomain() ) );

        mvc.perform(
                get(BASE_URL + "/1")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void update() throws Exception{

        when( serviceRepository.findById( any() ) ).thenReturn( Optional.of( serviceDomain() ) );
        when( serviceRepository.findFirstByNameIgnoreCase( any() ) ).thenReturn(Optional.empty() );

        mvc.perform(
                put(BASE_URL + "/1")
                        .content(objectMapper.writeValueAsString( serviceDTO() ))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteService() throws Exception{

        when( serviceRepository.findById( any() ) ).thenReturn( Optional.of( serviceDomain() ) );
        when( serviceLeaseRepository.findFirstByIdService( any() ) ).thenReturn( Optional.empty() );
        when( serviceWarehouseRepository.findFirstByServiceId( any() ) ).thenReturn( Optional.empty() );

        mvc.perform(
                delete(BASE_URL + "/1")
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAll() throws  Exception{

        Page<ServiceDomain> page = new PageImpl<>( List.of( serviceDomain() ) );

        when( serviceRepository.findAll( any(PageRequest.class) ) ).thenReturn( page );

        mvc.perform(
                get(BASE_URL)
        ).andExpect(status().isOk());

    }

    private ServiceDomain serviceDomain() {
        ServiceDomain serviceDomain = new ServiceDomain();
        serviceDomain.setId(1);
        serviceDomain.setName("Test");
        serviceDomain.setDescription("Test");
        serviceDomain.setPrice(100.00);
        return serviceDomain;
    }

    private ServiceDTO serviceDTO() {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(1);
        serviceDTO.setName("Test");
        serviceDTO.setDescription("Test");
        serviceDTO.setPrice(100.00);
        return serviceDTO;
    }

}
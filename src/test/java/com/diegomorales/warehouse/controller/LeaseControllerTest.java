package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.*;
import com.diegomorales.warehouse.dto.LeaseDTO;
import com.diegomorales.warehouse.repository.*;
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
class LeaseControllerTest {

    private final String BASE_URL = "/api/lease";
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LeaseRepository leaseRepository;
    @MockBean
    WarehouseRepository warehouseRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ServiceLeaseRepository serviceLeaseRepository;
    @MockBean
    ServiceWarehouseRepository serviceWarehouseRepository;
    @MockBean
    ServiceRepository serviceRepository;

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void save() throws Exception{

        when( warehouseRepository.findById( any() ) ).thenReturn( Optional.of( warehouseDomain() ) );
        when( userRepository.findById( any() ) ).thenReturn( Optional.of( userDomain() ) );
        when( leaseRepository.findByIdUserAndIdWarehouse( any(), any() ) ).thenReturn( Optional.empty() );
        when( leaseRepository.save( any() ) ).thenReturn( leaseDomain() );

        mvc.perform(
                post(BASE_URL)
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( leaseDTO() ) )
        ).andExpect( status().isCreated() );

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void findOne() throws Exception{

        when( leaseRepository.findById( any() ) ).thenReturn( Optional.of( leaseDomain() ) );
        when( serviceLeaseRepository.findAllByIdLease( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                get(BASE_URL + "/1")
        ).andExpect( status().isOk() );

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void deleteLease() throws Exception{

        when( leaseRepository.findById( any() ) ).thenReturn( Optional.of( leaseDomain() ) );
        when( serviceLeaseRepository.findAllByIdLease( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                delete(BASE_URL + "/1")
        ).andExpect( status().isOk() );

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void update() throws Exception{

        //Call findOne
        when( leaseRepository.findById( any() ) ).thenReturn( Optional.of( leaseDomain() ) );
        when( serviceLeaseRepository.findAllByIdLease( any() ) ).thenReturn( Collections.emptyList() );

        //Check user
        when( userRepository.findById( any() ) ).thenReturn( Optional.of( userDomain() ) );

        //Check warehouse
        when( warehouseRepository.findById( any() ) ).thenReturn( Optional.of( warehouseDomain() ) );
        when( serviceWarehouseRepository.findAllByWarehouseId( any() ) ).thenReturn( Collections.emptyList() );

        //Change Warehouse Availability if Changed
        when( warehouseRepository.findById( any() ) ).thenReturn( Optional.of( warehouseDomain() ) );

        mvc.perform(
                put( BASE_URL + "/1" )
                        .content( objectMapper.writeValueAsString( leaseDTO() ) )
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect( status().isOk() );

    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    void findAll() throws Exception{

        Page<Lease> page = new PageImpl<>( List.of( leaseDomain() ) );

        when( leaseRepository.findAll( any( PageRequest.class) ) ).thenReturn( page );

        //Call findOne
        when( leaseRepository.findById( any() ) ).thenReturn( Optional.of( leaseDomain() ) );
        when( serviceLeaseRepository.findAllByIdLease( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    private Lease leaseDomain(){
        Lease lease = new Lease();
        lease.setId(1);
        lease.setIdWarehouse(1);
        lease.setIdUser(1);
        lease.setActive(true);
        lease.setTotal(100.0);
        return lease;
    }

    private LeaseDTO leaseDTO(){
        LeaseDTO leaseDTO = new LeaseDTO();
        leaseDTO.setId(1);
        leaseDTO.setIdWarehouse(1);
        leaseDTO.setIdUser(1);
        leaseDTO.setActive(true);
        leaseDTO.setTotal(100.0);
        leaseDTO.setExtraServices(Collections.emptyList());
        return leaseDTO;
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

    private UserDomain userDomain() {
        UserDomain userDomain = new UserDomain();
        userDomain.setActive(true);
        userDomain.setId(1);
        userDomain.setPassword("1234");
        userDomain.setEmail("test@test");
        userDomain.setUsername("test");
        return userDomain;
    }

}
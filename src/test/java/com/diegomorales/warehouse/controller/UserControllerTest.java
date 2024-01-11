package com.diegomorales.warehouse.controller;

import com.diegomorales.warehouse.domain.Role;
import com.diegomorales.warehouse.domain.UserDomain;
import com.diegomorales.warehouse.dto.UserDTO;
import com.diegomorales.warehouse.repository.LeaseRepository;
import com.diegomorales.warehouse.repository.RoleRepository;
import com.diegomorales.warehouse.repository.UserRepository;
import com.diegomorales.warehouse.repository.UserRoleRepository;
import com.diegomorales.warehouse.security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class UserControllerTest {

    private final static String BASE_URL = "/api/user";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private UserRoleRepository userRoleRepository;
    @MockBean
    private LeaseRepository leaseRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception {

        when(userRepository.findFirstByEmailContainsIgnoreCase( any() ) ).thenReturn(Optional.empty() );
        when(userRepository.findFirstByUsernameIgnoreCase( any() ) ).thenReturn(Optional.empty() );
        when(userRepository.save( any() ) ).thenReturn( userDomain() );
        when(roleRepository.findFirsByNameContainsIgnoreCase( any() ) ).thenReturn(Optional.of( role() ) );

        mvc.perform(
                post(BASE_URL)
                        .content(objectMapper.writeValueAsString(userDTO()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveWithDefaultRole() throws Exception{
        when(userRepository.findFirstByEmailContainsIgnoreCase( any() ) ).thenReturn(Optional.empty());
        when(userRepository.findFirstByUsernameIgnoreCase( any() ) ).thenReturn(Optional.empty());
        when(userRepository.save( any())).thenReturn( userDomain() );
        when(roleRepository.findFirsByNameContainsIgnoreCase( any() ) ).thenReturn(Optional.of(role()));

        mvc.perform(
                post(BASE_URL)
                        .content( objectMapper.writeValueAsString( userDTO())  )
                        .contentType( MediaType.APPLICATION_JSON )
        ).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOne() throws Exception {
        when( userRepository.findById( any() ) ).thenReturn( Optional.of(userDomain()) );
        when( userRoleRepository.findByUserId( any() ) ).thenReturn(Collections.emptyList() );

        mvc.perform(
                get(BASE_URL + "/1")
                        .content( objectMapper.writeValueAsString(userDTO()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void update() throws Exception {

        when( userRepository.findById( any() ) ).thenReturn( Optional.of(userDomain()) );
        when( roleRepository.findFirsByNameContainsIgnoreCase( any()) ) .thenReturn( Optional.of( role())  );
        when( userRoleRepository.findByUserId( any() ) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                put(BASE_URL + "/1")
                        .content( objectMapper.writeValueAsString( userDTO()) )
                        .contentType( MediaType.APPLICATION_JSON )
        ).andExpect( status().isOk() );

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void disable() throws Exception{

        when( userRepository.findById( any() ) ).thenReturn( Optional.of( userDomain()) );
        when( leaseRepository.findAllByIdUser( any()) ).thenReturn( Collections.emptyList() );

        mvc.perform(
                put(BASE_URL + "/disable/1")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( userDTO() ) )
        ).andExpect( status().isOk() );

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAll() throws Exception{

        Page<UserDomain> page = new PageImpl<>(List.of(userDomain()));

        when( userRepository.findAll( any(PageRequest.class) ) ).thenReturn( page );
        when( userRepository.findById( any() ) ).thenReturn( Optional.of(userDomain()) );
        when( userRoleRepository.findByUserId( any() ) ).thenReturn(Collections.emptyList() );

        mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void profile() throws Exception{

        String token = "Bearer " + jwtUtils.generateAccessToken("admin");
        log.warn("Token: " + token);

        when( userRepository.findFirstByUsernameIgnoreCase( any() ) ).thenReturn( Optional.of( userDomain() ) );
        when( userRepository.findById( any() ) ).thenReturn( Optional.of(userDomain()) );
        when( userRoleRepository.findByUserId( any() ) ).thenReturn(Collections.emptyList() );

        mvc.perform(
                get(BASE_URL + "/profile")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

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

    private UserDTO userDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setUsername("test");
        userDTO.setPassword("1234");
        userDTO.setEmail("test@test");
        userDTO.setActive(true);
        userDTO.setRoles(List.of("ADMIN"));
        return userDTO;
    }

    private Role role(){
        Role role = new Role();
        role.setId(1);
        role.setName("ADMIN");
        role.setDescription("ADMIN");
        return role;
    }

}
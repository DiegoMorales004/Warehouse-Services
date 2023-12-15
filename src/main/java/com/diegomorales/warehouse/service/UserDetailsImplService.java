package com.diegomorales.warehouse.service;

import com.diegomorales.warehouse.domain.UserDomain;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDetailsImplService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDomain userDomain = this.userRepository.findFirstByUsernameContainsIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user does not exits"));

        List<String> roles;

        try {
             roles = this.userRoleService.findAllRolesByUser(userDomain.getId());

        } catch (GenericException e) {
            throw new RuntimeException(e);
        }

        Collection<? extends GrantedAuthority> authorities = roles.stream().map(
                role ->
                        new SimpleGrantedAuthority("ROLE_" + role)
        ).collect(Collectors.toSet());



        return new User(
                userDomain.getUsername(),
                userDomain.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}

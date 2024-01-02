package com.diegomorales.warehouse.security.filters;

import com.diegomorales.warehouse.security.jwt.JwtUtils;
import com.diegomorales.warehouse.service.UserDetailsImplService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsImplService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException, UsernameNotFoundException, BadCredentialsException {

        try {
            String tokenHeader = request.getHeader("Authorization");

            if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
                String token = tokenHeader.substring(7);

                if (jwtUtils.isTokenValid(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    if(!userDetails.isEnabled()){
                        throw new BadCredentialsException("The user is disable");
                    }

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new AuthenticationException("Invalid token");
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().print(e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        } catch (BadCredentialsException e) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getOutputStream().print(e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
    }
}

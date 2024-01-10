package com.diegomorales.warehouse.utils;

import com.diegomorales.warehouse.dto.UserDTO;
import com.diegomorales.warehouse.exception.BadRequestException;
import com.diegomorales.warehouse.exception.GenericException;
import com.diegomorales.warehouse.security.jwt.JwtUtils;
import com.diegomorales.warehouse.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenUtils {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserDTO getUserToken(HttpServletRequest httpServletRequest) throws GenericException, BadRequestException {


            String authorization = httpServletRequest.getHeader("Authorization");

            String token = authorization.substring(7);

            //Get userName from the token
            String userNameToken = this.jwtUtils.getUsernameFromToken(token);

            return this.userService.findUserByUserName(userNameToken);
    }

}

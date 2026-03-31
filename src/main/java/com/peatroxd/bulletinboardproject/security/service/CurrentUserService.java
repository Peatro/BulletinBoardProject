package com.peatroxd.bulletinboardproject.security.service;

import com.peatroxd.bulletinboardproject.common.exception.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    public UUID getUserId() {
        Jwt jwt = getJwt();
        return UUID.fromString(jwt.getSubject());
    }

    public String getUsername() {
        Jwt jwt = getJwt();
        return jwt.getClaimAsString("preferred_username");
    }

    private Jwt getJwt() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof Jwt jwt)) {
            throw new UnauthorizedException("No JWT found in security context");
        }

        return jwt;
    }
}

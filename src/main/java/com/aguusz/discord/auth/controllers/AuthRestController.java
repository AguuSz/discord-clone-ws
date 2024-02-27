package com.aguusz.discord.auth.controllers;

import ar.edu.iua.iw3.backend.auth.custom.CustomAuthenticationManager;
import ar.edu.iua.iw3.backend.auth.filters.AuthConstants;
import ar.edu.iua.iw3.backend.controllers.BaseRestController;
import ar.edu.iua.iw3.backend.controllers.Constants;
import ar.edu.iua.iw3.backend.model.Role;
import ar.edu.iua.iw3.backend.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class AuthRestController extends BaseRestController {
    @Autowired
    private AuthenticationManager authManager;

    @PostMapping(value = Constants.URL_LOGIN, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> loginExternalOnlyToken(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        Authentication auth = null;
        try {
            auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
        } catch (AuthenticationServiceException e0) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        User user = (User) auth.getPrincipal();
        List<String> roles = new ArrayList<String>();
        for (Role r : user.getRoles()) {
            roles.add(r.getName());
        }
        String token = JWT.create().withSubject(user.getUsername())
                .withClaim("roles", roles)
                .withClaim("email", user.getEmail())
                .withClaim("name", user.getName())
                .withClaim("username", user.getUsername())
                .withClaim("lastname", user.getLastname())
                .withClaim("fullName", user.getName() + " " + user.getLastname())
                .withClaim("version", "1.0.0")
                .withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }
}

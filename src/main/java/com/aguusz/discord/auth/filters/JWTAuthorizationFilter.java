package com.aguusz.discord.auth.filters;

import com.aguusz.discord.models.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(AuthConstants.AUTH_HEADER_NAME);
        String param = req.getParameter(AuthConstants.AUTH_PARAM_NAME);

        boolean byHeader = !(header == null || !header.startsWith(AuthConstants.TOKEN_PREFIX));
        boolean byParam = !(param == null || param.trim().length() < 10);

        // Si no se envia o es correcto el inicio de la cabecera o bien no se envia un
        // parametro correcto, se continua con el resto de los filtros
        if (!byHeader && !byParam) {
            chain.doFilter(req, res);
            return;
        }

        // Le dmaos prioridad al header
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req, byHeader);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    // Extraer el token JWT de la cabecera y lo intenta validar
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, boolean byHeader) {
        // Recordar que el header inicia con alguna cadena, por ejemplo: 'Bearer '
        String token = byHeader
                ? request.getHeader(AuthConstants.AUTH_HEADER_NAME).replace(AuthConstants.TOKEN_PREFIX, "")
                : request.getParameter(AuthConstants.AUTH_PARAM_NAME);

        if (token != null) {
            // Parseamos el token usando la libreria
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()))
                    .build()
                    .verify(token);

//            log.info("Token recibido por '{}'", byHeader ? "header" : "param");
//            log.info("Usuario logueado: " + jwt.getSubject());
//            log.info("Roles: " + jwt.getClaim("roles"));
//            log.info("Custom JWT Version: " + jwt.getClaim("version").asString());
//
//            log.info("Username: " + jwt.getClaim("username").asString());
//            log.info("User name: " + jwt.getClaim("name").asString());
//            log.info("User lastname: " + jwt.getClaim("lastname").asString());
//            log.info("User fullname: " + jwt.getClaim("fullName").asString());
//            log.info("Custom JWT Version: " + jwt.getClaim("version").asString());

            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            try {
                @SuppressWarnings("unchecked")
                        List<String> rolesStr = (List<String>) jwt.getClaim("roles").as(List.class);
                authorities = rolesStr.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
            } catch (Exception e) {

            }

            String username = jwt.getSubject();

            if (username != null) {
                User user = new User();
                user.setUsername(username);
                user.setEmail(jwt.getClaim("email").asString());
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
            return null;
        }
        return null;
    }
}

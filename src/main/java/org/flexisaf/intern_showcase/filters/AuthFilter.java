package org.flexisaf.intern_showcase.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.flexisaf.intern_showcase.request.LoginRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),LoginRequest.class);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

            setDetails(request,usernamePasswordAuthenticationToken);

            return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        }
        catch(IOException ioException) {
            log.error("an error occurred:!!, {}",ioException.getMessage());
        }
        return null;
    }
}

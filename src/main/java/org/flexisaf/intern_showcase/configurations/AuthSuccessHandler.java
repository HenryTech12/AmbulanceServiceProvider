package org.flexisaf.intern_showcase.configurations;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object[] roles = authentication.getAuthorities().toArray();
        for(Object role: roles) {
            System.out.println("ROLES: "+role);
        }
        if(roles[0].toString().equals("ROLE_Admin"))
            response.sendRedirect("/admin/api/dashboard");
        else
            response.sendRedirect("/api/user/request");
    }
}

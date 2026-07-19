package com.integrador1.config; // Ajusta a tu paquete real

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class UserLoggingFilter implements Filter {

    private static final String USER_KEY = "username";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 1. Obtener el usuario autenticado de Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() 
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                // Guardamos el nombre de usuario en el MDC de SLF4J
                MDC.put(USER_KEY, authentication.getName());
            } else {
                MDC.put(USER_KEY, "Anónimo");
            }

            chain.doFilter(request, response);
        } finally {
            // 2. MUY IMPORTANTE: Limpiar el MDC al terminar la petición para evitar fugas de memoria
            MDC.remove(USER_KEY);
        }
    }
}
package com.integrador1.security;

import com.integrador1.service.MyAppUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyAppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            MyAppUserService appUserService,
            PasswordEncoder passwordEncoder) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Control de accesos (Público vs Protegido)
                .authorizeHttpRequests(auth -> auth
                        // 1. Recursos públicos y vistas de registro/login
                        .requestMatchers(
                                "/login",
                                "/signup",
                                "/req/signup",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/images/**",    
                                "/fragments/**",   
                                "/uploads/**",       
                                "/h2-console/**"
                        ).permitAll()

                        .requestMatchers("/monitoring/**").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers("/reports/**", "/reports-api/**").hasAnyRole("ADMIN", "OWNER")

                        .anyRequest().authenticated()
                )

                // Formulario de Login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Control de Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // Deshabilitar CSRF (Permite peticiones AJAX POST del backup sin problemas de tokens)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Permitir H2-Console y iframes del mismo origen
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}
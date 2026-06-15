package com.integrador1.security;

import com.integrador1.config.PasswordConfig;
import com.integrador1.repository.MyAppUserRepository;
import com.integrador1.service.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Control de accesos (Público vs Protegido)
                .authorizeHttpRequests(auth -> auth
                        // Únicamente lo que se puede ver sin haber iniciado sesión
                        .requestMatchers(
                                "/login",
                                "/signup",
                                "/req/signup",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/h2-console/**"
                        ).permitAll()

                        // OBLIGA a que el Dashboard, Rental, Maintenance, etc., requieran loguearse
                        .anyRequest().authenticated()
                )

                // 2. Formulario de Login configurado correctamente
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        // defaultSuccessUrl con 'true' fuerza a Spring a guardar la sesión antes de redirigir
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // 3. Control de Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
        }
}
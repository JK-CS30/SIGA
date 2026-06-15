package com.integrador1.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "my_app_user")
public class MyAppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Nombre y Apellido
    private String correo;
    private String password;
    private String rol;

    // ==========================================
    //   MÉTODOS OBLIGATORIOS DE USERDETAILS
    // ==========================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Le agregamos el prefijo ROLE_ dinámicamente para que coincida con SecurityConfig
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        // TRUCO: Retornamos el Nombre y Apellido. Así Thymeleaf pintará esto en el #authentication.name
        return this.username; 
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Cambiar a true para que no bloquee la cuenta
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Cambiar a true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Cambiar a true
    }

    @Override
    public boolean isEnabled() {
        return true; // Cambiar a true
    }

    // ==========================================
    //            GETTERS Y SETTERS
    // ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // El setter de username normal para el registro
    public void setUsername(String username) { this.username = username; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
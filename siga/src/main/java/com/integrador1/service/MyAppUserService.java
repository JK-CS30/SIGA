package com.integrador1.service;

import com.integrador1.model.MyAppUser;
import com.integrador1.repository.MyAppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyAppUserService
        implements UserDetailsService {

    private final MyAppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public MyAppUserService(
            MyAppUserRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuario(
            String username,
            String correo,
            String password) {

        MyAppUser user = new MyAppUser();

        user.setUsername(username);
        user.setCorreo(correo);

        // Guarda la contraseña cifrada
        user.setPassword(
                passwordEncoder.encode(password));

        repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        MyAppUser user = repository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No encontrado"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public List<MyAppUser> listarUsuarios() {
        return repository.findAll();
    }

}
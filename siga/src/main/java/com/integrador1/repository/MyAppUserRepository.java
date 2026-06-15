package com.integrador1.repository;

import com.integrador1.model.MyAppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyAppUserRepository extends JpaRepository<MyAppUser, Long> {

    Optional<MyAppUser> findByCorreo(String correo);
}

package com.integrador1.service;

import com.integrador1.model.MyAppUser;
import com.integrador1.repository.MyAppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyAppUserService implements UserDetailsService {

    private final MyAppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Inyección por constructor (Práctica recomendada)
    public MyAppUserService(MyAppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema SIGA.
     * @param nombreApellido Se guarda en la columna 'username' de la BD
     * @param correo Se usa como credencial única de acceso
     * @param password Contraseña en texto plano que será cifrada
     * @param rol Rol dinámico seleccionado en el formulario (ADMIN, OWNER, MECANICO, etc.)
     */
    public void registrarUsuario(String nombreApellido, String correo, String password, String rol) {
        MyAppUser user = new MyAppUser();

        user.setUsername(nombreApellido); // Guardamos Nombre y Apellido en el campo username
        user.setCorreo(correo);
        user.setRol(rol.toUpperCase()); // Aseguramos que se guarde en mayúsculas (convención de Spring)

        // Ciframos la contraseña antes de guardarla en la base de datos
        user.setPassword(passwordEncoder.encode(password));

        repository.save(user);
    }

    /**
     * Método requerido por Spring Security para el Login.
     * Aunque el parámetro se llame 'username', Spring Security le pasará el correo 
     * gracias al ajuste que hicimos en el input name="username" del login.html.
     */
    @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            // Buscamos en la base de datos usando el correo que viene del login
            return repository.findByCorreo(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario con correo " + email + " no encontrado"));
        }

    public List<MyAppUser> listarUsuarios() {
        return repository.findAll();
    }
}
package com.integrador1.controller;

import com.integrador1.service.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private MyAppUserService userService;

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registrarUsuario(
            @RequestParam String username,
            @RequestParam String correo,
            @RequestParam String password) {

        System.out.println("=== REGISTRO ===");
        System.out.println(username);
        System.out.println(correo);
        System.out.println(password);


        userService.registrarUsuario(
                username,
                correo,
                password);

        return "redirect:/login";
    }

}
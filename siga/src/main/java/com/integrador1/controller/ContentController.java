package com.integrador1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ContentController {

    @GetMapping("/login")
    public String login(){

        return "login";
    }

    /*@GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }*/

}

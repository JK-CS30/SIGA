package com.integrador1.controller;

import com.integrador1.model.MyAppUser;
import com.integrador1.repository.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @PostMapping(value="/req/signup", consumes="aaplication/json")
    public MyAppUser createUser(@RequestBody MyAppUser user){
        return myAppUserRepository.save(user);
    }
}

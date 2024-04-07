package com.example.controller;

import com.example.dto.CustomerRequestDto;
import com.example.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class CustomerController {
    private final CustomerService customerService;
    public CustomerController (CustomerService customerService){
        this.customerService = customerService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CustomerRequestDto customerRequestDto){
        return customerService.registerCustomer(customerRequestDto.getEmail(), customerRequestDto.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CustomerRequestDto customerRequestDto){
        return customerService.login(customerRequestDto.getEmail(), customerRequestDto.getPassword());
    }
}

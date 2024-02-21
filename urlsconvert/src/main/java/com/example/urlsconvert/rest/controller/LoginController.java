package com.example.urlsconvert.rest.controller;

import com.example.urlsconvert.entity.Customer;
import com.example.urlsconvert.dao.CustomerRepository;
import com.example.urlsconvert.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    //private CustomerRepository customerRepository;
    private RegistrationService registrationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        try {
            String hashPwd = passwordEncoder.encode(customer.getPassword());
            customer.setPassword(hashPwd);
            Customer savedCustomer = registrationService.registerNewCustomer(customer.getEmail(), customer.getPassword());
            if(savedCustomer != null && savedCustomer.getId() > 0){
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("Given user details are successfully registered");
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Unable to register the user");

            }
        } catch (Exception ex){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occrued due to " + ex.getMessage());
        }
    }
}

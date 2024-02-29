package com.example.urlsconvert.rest.controller;

import com.example.urlsconvert.entity.Customer;
import com.example.urlsconvert.dao.CustomerRepository;
import com.example.urlsconvert.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        if(customer.getRole() == null || customer.getRole().trim().isEmpty()){
            customer.setRole("ROLE_USER");
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Customer savedCustomer;
        try {
            savedCustomer = customerRepository.save(customer);
            if(savedCustomer.getId() > 0){
//                authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(savedCustomer.getEmail(), savedCustomer.getPassword())
//                );
                // Generate JWT Token
                final String jwtToken = jwtUtil.generateToken(savedCustomer.getEmail());

                return ResponseEntity.ok("Token: " + jwtToken);
            }
        } catch (Exception ex){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occurred due to " + ex.getMessage());
        }

        return ResponseEntity.badRequest().body("Failed to register");
    }
}

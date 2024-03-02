package com.example.urlsconvert.rest.controller;

import com.example.urlsconvert.entity.Customer;
import com.example.urlsconvert.dao.CustomerRepository;
import com.example.urlsconvert.rest.CustomAuthenticationException;
import com.example.urlsconvert.rest.RegistrationException;
import com.example.urlsconvert.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/register")
    public String getRegister(){
        return "register";
    }

    @GetMapping("/login")
    public String getLogin(){
        return "login";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        if(customer.getEmail() == null || customer.getEmail().isEmpty() || !customerRepository.findByEmail(customer.getEmail()).isEmpty()){
            //throw new RegistrationException("Email is already in use or invalid.");
            throw new RegistrationException("{\"error\": \"" + "Email is already in use or invalid." + "\"}");
        }
        if(customer.getRole() == null || customer.getRole().trim().isEmpty()){
            customer.setRole("ROLE_USER");
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Customer savedCustomer;
        try {
            savedCustomer = customerRepository.save(customer);
            if(savedCustomer.getId() > 0){

                // Generate JWT Token
                final String jwtToken = jwtUtil.generateToken(savedCustomer.getEmail());

                //return ResponseEntity.ok("Token: " + jwtToken);
                return ResponseEntity.ok(jwtToken);
            }
        } catch (Exception ex){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occurred due to " + ex.getMessage());
        }

        return ResponseEntity.badRequest().body("Failed to register");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Customer loginCustomer){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginCustomer.getEmail(), loginCustomer.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final String jwtToken = jwtUtil.generateToken(loginCustomer.getEmail());

            return ResponseEntity.ok(jwtToken);
        } catch (BadCredentialsException ex){
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password");
            throw new CustomAuthenticationException("Incorrect email or password");
        }
    }
}

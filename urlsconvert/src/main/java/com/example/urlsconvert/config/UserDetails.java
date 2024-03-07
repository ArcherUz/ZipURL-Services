package com.example.urlsconvert.config;

import com.example.urlsconvert.entity.Customer;
import com.example.urlsconvert.dao.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetails implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User detail not found for the user : "+ username));

        String password = customer.getPassword();
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(customer.getRole()));
        String role = customer.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "ROLE_USER"; // Default role
        }
        authorityList.add(new SimpleGrantedAuthority(role));

        return new User(username, password, authorityList);

    }
//    @Override
//    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Customer customer = customerRepository.findByEmail(email).get(0);
//        if (customer == null) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//
//        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(customer.getRole()));
//
//        return new User(customer.getEmail(), customer.getPassword(), authorities);
//    }
}

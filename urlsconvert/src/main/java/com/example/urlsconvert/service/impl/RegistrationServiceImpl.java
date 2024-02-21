package com.example.urlsconvert.service.impl;

import com.example.urlsconvert.dao.AuthorityRepository;
import com.example.urlsconvert.dao.CustomerRepository;
import com.example.urlsconvert.entity.Authority;
import com.example.urlsconvert.entity.Customer;
import com.example.urlsconvert.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Override
    @Transactional
    public Customer registerNewCustomer(String email, String password) {
        Authority userAuthority = authorityRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Authority newUserRole = new Authority("ROLE_USER");
                    return authorityRepository.save(newUserRole);
                });

        Customer newCustomer = new Customer();
        newCustomer.setEmail(email);
        newCustomer.setPassword(password);
        newCustomer.setAuthorities(new HashSet<>(Collections.singleton(userAuthority)));

        return customerRepository.save(newCustomer);
    }
}

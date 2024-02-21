package com.example.urlsconvert.service;

import com.example.urlsconvert.entity.Customer;

public interface RegistrationService {
    Customer registerNewCustomer(String email, String password);
}

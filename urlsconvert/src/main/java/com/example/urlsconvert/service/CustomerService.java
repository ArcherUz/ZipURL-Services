package com.example.urlsconvert.service;

import com.example.urlsconvert.entity.Customer;
import com.example.urlsconvert.entity.UrlLongToShort;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<Customer> getCustomerByEmail(String email);
    List<UrlLongToShort> getCustomerUrls(int customerId);
}

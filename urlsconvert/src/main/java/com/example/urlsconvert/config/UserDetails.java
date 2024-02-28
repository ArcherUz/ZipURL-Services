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
import java.util.List;

@Service
public class UserDetails implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password;
        List<GrantedAuthority> authorityList;
        List<Customer> customers = customerRepository.findByEmail(username);
        System.out.println(customers);
        System.out.println(username);
        if(customers.isEmpty()){
            throw new UsernameNotFoundException("User detail not found for the user : "+ username);

        } else {
            password = customers.get(0).getPassword();
            authorityList = new ArrayList<>();
            authorityList.add(new SimpleGrantedAuthority(customers.get(0).getRole()));

        }
        return new User(username, password, authorityList);
    }
}

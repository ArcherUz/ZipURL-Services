package com.example.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private List<String> longUrls;

    public Customer() {
        longUrls = new ArrayList<>();
    }

    public Customer(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getLongUrls() {
        return longUrls;
    }

    public void setLongUrls(List<String> longUrls) {
        this.longUrls = longUrls;
    }

    public void addLongUrl(String longUrl){
        this.longUrls.add(longUrl);
    }
}

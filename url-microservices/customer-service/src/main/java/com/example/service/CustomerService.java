package com.example.service;

import com.example.entity.Customer;
import com.example.repository.CustomerRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }
    public ResponseEntity<String> registerCustomer(String email, String password){
        String accessToken = getAdminAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        String usersUrl = "http://localhost:8181/admin/realms/spring-boot-microservices-realm/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", email);
        userMap.put("enabled", true);
        userMap.put("email", email);
        userMap.put("emailVerified", true);
        userMap.put("credentials", Collections.singletonList(Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        )));
        userMap.put("requiredActions", Collections.emptyList());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userMap, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(usersUrl, request, String.class);
        } catch (HttpClientErrorException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }

        Customer customer = new Customer();
        customer.setEmail(email);
        customerRepository.save(customer);

        return login(email, password);
    }

    public ResponseEntity<String> login(String email, String password){
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "http://localhost:8181/realms/spring-boot-microservices-realm/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", "spring-boot-client");
        map.add("client_secret", "l9aSX7NRFPGqvEXudlajDWPuyylQEx7x");
        map.add("username", email);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }

    }

    private String getAdminAccessToken(){
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "http://localhost:8181/realms/master/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "admin-cli");
        map.add("username", "admin");
        map.add("password", "admin");
        map.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return response.getBody().get("access_token").toString();
    }



}

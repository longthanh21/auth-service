package com.example.authservice.client;

import com.example.authservice.dto.request.UserRequest;
import com.example.authservice.dto.response.UserByAccountIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {

    @PostMapping("/api/users")
    void createUser(@RequestBody UserRequest userRequest);

    @GetMapping("/api/users/get-user-by-account-id/{accountId}")
    UserByAccountIdResponse getUserByAccountId(@PathVariable Long accountId);

}

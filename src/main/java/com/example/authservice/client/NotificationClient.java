package com.example.authservice.client;

import com.example.authservice.dto.request.SendRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "http://localhost:1234")
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    void sendVerificationEmail(@RequestBody SendRequest request);
}


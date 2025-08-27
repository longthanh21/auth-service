package com.example.authservice.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SendBackOtpRequest {

    @Email
    private String email;
}

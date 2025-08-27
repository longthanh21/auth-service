package com.example.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotNull(message = "AccountId không được null")
    private Long accountId;

    @NotBlank
    private String fullName;

    private String phone;

    private LocalDate dob;

    private String address;
}

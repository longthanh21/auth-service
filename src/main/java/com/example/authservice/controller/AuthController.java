package com.example.authservice.controller;

import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.request.SendBackOtpRequest;
import com.example.authservice.dto.request.VerifyRequest;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Đăng ký thành công. Vui lòng kiểm tra email để xác thực OTP.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody @Valid VerifyRequest request) {
        authService.verify(request);
        return ResponseEntity.ok("Xác thực email thành công. Tài khoản đã được kích hoạt.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/send-back-otp")
    public ResponseEntity<String> sendBackOtp(@Valid @RequestBody SendBackOtpRequest request){
        authService.sendBackOtp(request);
        return ResponseEntity.ok("Đã gửi lại mã OTP thành công. Vui lòng kiểm tra email để xác thực lại OTP.");
    }
}


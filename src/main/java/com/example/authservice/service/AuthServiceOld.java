//package com.example.authservice.service;
//
//import com.example.authservice.entity.Account;
//import com.example.authservice.repository.AccountRepository;
//import com.example.authservice.security.jwt.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceOld {
//
//    private final AuthenticationManager authenticationManager;
//    private final JwtService jwtService;
//    private final AccountRepository accountRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final EmailOtpService emailOtpService;
//
//    public void register(RegisterRequest request) {
//        if (!studentRepository.findByStudentCode(request.getStudentCode()).isPresent()) {
//            throw new RuntimeException("Mã sinh viên không hợp lệ.");
//        }
//
//        if (accountRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email đã tồn tại.");
//        }
//
//        Account account = Account.builder()
//                .studentCode(request.getStudentCode())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.USER)
//                .isActive(true)
//                .isEmailVerified(false)
//                .createdAt(LocalDateTime.now())
//                .build();
//        accountRepository.save(account);
//
//        emailOtpService.generateAndSendOtp(request.getEmail(), OtpType.REGISTER);
//    }
//
//    public void verifyOtp(VerifyOtpRequest request) {
//        emailOtpService.verifyOtp(request.getEmail(), request.getOtpCode());
//    }
//
//    public LoginResponse login(LoginRequest request) {
//        Account account = accountRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("Email không tồn tại."));
//
//        if (!account.isEmailVerified()) {
//            throw new RuntimeException("Tài khoản chưa xác thực email.");
//        }
//
//        authenticationManager.authenticate(
//                new AccountnamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );
//
//        String token = jwtService.generateToken(account);
//        return new LoginResponse(token, account.getRole().name());
//    }
//
//}

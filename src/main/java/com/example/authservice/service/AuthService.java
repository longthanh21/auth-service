package com.example.authservice.service;

import com.example.authservice.client.NotificationClient;
import com.example.authservice.client.UserClient;
import com.example.authservice.dto.request.*;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.dto.response.UserByAccountIdResponse;
import com.example.authservice.entity.Account;
import com.example.authservice.entity.Verification;
import com.example.authservice.exception.BadRequestException;
import com.example.authservice.repository.AccountRepository;
import com.example.authservice.repository.VerificationRepository;
import com.example.authservice.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AccountRepository accountRepository;
    private final VerificationRepository verificationRepo;
    private final PasswordEncoder passwordEncoder;
    private final NotificationClient notificationClient;
    private final UserClient userClient;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Đăng ký
    public void register(RegisterRequest req) {
//        if (accountRepository.findByEmail(req.getEmail()).isPresent()) {
//            throw new BadRequestException("Email đã tồn tại");
//        }

        if(accountRepository.findByUserCode(req.getUserCode()) != null){
            throw new BadRequestException("Student code đã tồn tại");
        }

        Account account = new Account();
        account.setEmail(req.getEmail());
        account.setUserCode(req.getUserCode());
        account.setPassword(passwordEncoder.encode(req.getPassword()));
        account.setRole(0); // USER mặc định
        account.setStatus(0); // chưa xác minh
        account = accountRepository.save(account);

        // tạo OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        Verification v = new Verification();
        v.setAccount(account);
        v.setOtpCode(otp);
        v.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        verificationRepo.save(v);

        // gọi user-service để lưu thông tin
        UserRequest userRequest = new UserRequest(account.getId(), req.getFullName(), req.getPhone(), req.getDob(), req.getAddress());
        log.info("Thong tin user moi: " + userRequest);
        userClient.createUser(userRequest);

        // gửi email qua notification-service
        SendRequest sendReq = new SendRequest();
        sendReq.setRecipient(req.getEmail());
        sendReq.setTemplateCode("VERIFY_EMAIL");
        sendReq.setParams(Map.of("code", otp, "minutes", "5", "username", req.getFullName()));
        sendReq.setDedupeKey("verify-" + req.getEmail());

        notificationClient.sendVerificationEmail(sendReq);
    }

    // Xác minh OTP
    public void verify(VerifyRequest req) {
        Account account = accountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy tài khoản"));

        Verification v = verificationRepo
                .findByAccountIdAndOtpCodeAndUsedFalseAndStatus(account.getId(), req.getOtpCode(), 1)
                .orElseThrow(() -> new BadRequestException("OTP không hợp lệ"));

        if (v.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP đã hết hạn");
        }

        v.setUsed(true);
        verificationRepo.save(v);

        account.setStatus(1);
        accountRepository.save(account);

        UserByAccountIdResponse user = userClient.getUserByAccountId(account.getId());
        String fullname = user.getFullName();

        SendRequest sendReq = new SendRequest();
        sendReq.setRecipient(req.getEmail());
        sendReq.setTemplateCode("VERIFY_SUCCESS");
        sendReq.setParams(Map.of( "username", fullname));
        sendReq.setDedupeKey("verify-" + req.getEmail());

        notificationClient.sendVerificationEmail(sendReq);
    }

    public void sendBackOtp(SendBackOtpRequest request){
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy tài khoản"));

        Verification v = verificationRepo
                .findByAccountIdAndUsedFalseAndStatus(account.getId(), 1)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy ID tài khoản"));

        v.setStatus(0);
        verificationRepo.save(v);

        String otp = String.format("%06d", new Random().nextInt(999999));
        Verification v1 = new Verification();
        v1.setAccount(account);
        v1.setOtpCode(otp);
        v1.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        verificationRepo.save(v1);

        UserByAccountIdResponse user = userClient.getUserByAccountId(account.getId());
        String fullname = user.getFullName();

        SendRequest sendReq = new SendRequest();
        sendReq.setRecipient(request.getEmail());
        sendReq.setTemplateCode("VERIFY_EMAIL");
        sendReq.setParams(Map.of("code", otp, "minutes", "5", "username", fullname));
        sendReq.setDedupeKey("verify-" + request.getEmail());

        notificationClient.sendVerificationEmail(sendReq);
    }

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại."));

        if (account.getStatus() == 0) {
            throw new RuntimeException("Tài khoản chưa xác thực email.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtService.generateToken(account.getEmail(), mapRole(account.getRole()));
        return new LoginResponse(token, mapRole(account.getRole()));
    }

    private String mapRole(Integer role) {
        return switch (role) {
            case 1 -> "ADMIN";
            case 2 -> "LIBRARIAN";
            case 0 -> "STUDENT";
            default -> "GUEST";
        };
    }
}

package com.example.authservice.repository;

import com.example.authservice.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findByAccountIdAndOtpCodeAndUsedFalseAndStatus(Long accountId, String otp, Integer status);

    Optional<Verification> findByAccountIdAndUsedFalseAndStatus(Long accountId, Integer status);
}

package com.example.authservice.security.jwt;

import com.example.authservice.entity.Account;
import com.example.authservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + mapRole(account.getRole())))
        );
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

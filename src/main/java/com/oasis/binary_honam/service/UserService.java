package com.oasis.binary_honam.service;

import com.oasis.binary_honam.dto.JoinRequest;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean ckeckPhoneDuplicate(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public void securityJoin(JoinRequest joinRequest) {
        joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));
        User user = joinRequest.toEntity();
        userRepository.save(user);
    }
}

package com.oasis.binary_honam.service;

import com.oasis.binary_honam.dto.JoinRequest;
import com.oasis.binary_honam.dto.User.*;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public FindEmailResponse findEmail(FindEmailRequest findEmailRequest) {
        User user = userRepository.findEmail(findEmailRequest.getName(), findEmailRequest.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return new FindEmailResponse(user.getEmail());
    }

    public boolean checkExistsUser(TempPasswordRequest tempPasswordRequest) {
        String name = tempPasswordRequest.getName();
        String phone = tempPasswordRequest.getPhone();
        String email = tempPasswordRequest.getEmail();
        return userRepository.existsByNameAndPhoneAndEmail(name, phone, email);
    }

    public String tempPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePw = encoder.encode(str); // 패스워드 암호화

        user.updatePassword(encodePw);
        userRepository.save(user);

        return str;
    }

    public getInfoResponse getInfo(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        getInfoResponse dto = getInfoResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .build();

        return dto;
    }

    public void updateInfo(UpdateInfoRequest updateInfoRequest, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        String name = updateInfoRequest.getName();
        String nickname = updateInfoRequest.getNickname();
        String phone = updateInfoRequest.getPhone();

        user.update(name, nickname, phone);
        userRepository.save(user);
    }

    public boolean updatePassword(SetPasswordRequest setPasswordRequest, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodePw = encoder.encode(setPasswordRequest.getPassword()); // 패스워드 암호화
            user.updatePassword(encodePw);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.User.*;
import com.oasis.binary_honam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/find-email")
    @Operation(summary = "아이디 찾기")
    public FindEmailResponse findEmail(FindEmailRequest findEmailRequest){
        return userService.findEmail(findEmailRequest);
    }

    @PostMapping("/temp-password")
    @Operation(summary = "임시 비밀번호 발급")
    public String tempPassword(TempPasswordRequest tempPasswordRequest) {
        if (userService.checkExistsUser(tempPasswordRequest)) {
            String temp = userService.tempPassword(tempPasswordRequest.getEmail());
            return "임시 비밀번호는 \"" + temp + "\" 입니다.";
        }else{
            return "존재하지않는 사용자 입니다.";
        }
    }

    @GetMapping("/info")
    @Operation(summary = "정보 조회")
    public getInfoResponse getInfo(Authentication authentication) {
        return userService.getInfo(authentication);
    }

    @PatchMapping("/info")
    @Operation(summary = "정보 수정")
    public ResponseEntity updateInfo(UpdateInfoRequest updateInfoRequest,
                                     Authentication authentication){
        userService.updateInfo(updateInfoRequest, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update-password")
    @Operation(summary = "비밀번호 재설정")
    public String updatePassword(SetPasswordRequest setPasswordRequest,
                              Authentication authentication) {
        if (!setPasswordRequest.getPassword().equals(setPasswordRequest.getPasswordCheck())) {
            return "비밀번호가 일치하지 않습니다.";
        }

        userService.updatePassword(setPasswordRequest, authentication);
        return "비밀번호가 재설정 되었습니다.";
    }
}

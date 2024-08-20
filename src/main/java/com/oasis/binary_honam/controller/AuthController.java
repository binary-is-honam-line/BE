package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.JoinRequest;
import com.oasis.binary_honam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/join")
    @Operation(summary = "유저 회원가입")
    public ResponseEntity<String> join(@Valid @RequestBody JoinRequest joinRequest, BindingResult bindingResult) {
        if (userService.checkEmailDuplicate(joinRequest.getEmail())) {
            bindingResult.addError(new FieldError("joinRequest", "email", "사용하고 있는 이메일 입니다."));
        }
        if (userService.ckeckPhoneDuplicate(joinRequest.getPhone())){
            bindingResult.addError(new FieldError("joinRequest", "phone", "사용하고 있는 번호입니다."));
        }
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        userService.securityJoin(joinRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}

package com.oasis.binary_honam.dto;

import com.oasis.binary_honam.entity.QuestAlbum;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.entity.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinRequest {
    @NotBlank(message = "이메일을 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @NotBlank(message = "닉네임을 입력하세요.")
    private String nickname;

    private String phone;

    public User toEntity(QuestAlbum questAlbum){
        return User.builder()
                .password(this.password)
                .name(this.name)
                .nickname(this.nickname)
                .email(this.email)
                .phone(this.phone)
                .role(Role.USER)
                .questAlbum(questAlbum)
                .build();
    }

    public void setPassword(String password){
        this.password = password;
    }
}

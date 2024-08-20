package com.oasis.binary_honam.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class getInfoResponse {
    private String name;
    private String nickname;
    private String email;
    private String phone;
}

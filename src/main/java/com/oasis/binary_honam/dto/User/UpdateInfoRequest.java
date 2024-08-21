package com.oasis.binary_honam.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateInfoRequest {
    private String name;
    private String nickname;
    private String phone;
}

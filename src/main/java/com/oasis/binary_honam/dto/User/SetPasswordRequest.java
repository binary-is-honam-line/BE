package com.oasis.binary_honam.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetPasswordRequest {
    private String password;
    private String passwordCheck;
}

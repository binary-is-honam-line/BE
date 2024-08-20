package com.oasis.binary_honam.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TempPasswordRequest {
    private String name;
    private String email;
    private String phone;
}

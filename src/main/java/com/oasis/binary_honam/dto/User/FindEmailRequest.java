package com.oasis.binary_honam.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindEmailRequest {
    private String name;
    private String phone;
}

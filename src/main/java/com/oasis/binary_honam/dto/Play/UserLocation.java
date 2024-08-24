package com.oasis.binary_honam.dto.Play;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLocation {
    private BigDecimal lat;
    private BigDecimal lng;
}

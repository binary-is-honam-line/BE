package com.oasis.binary_honam.dto.Spot;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SpotPointResponse {
    private int sequenceNumber;
    private Long spotId;
    private String spotName;
    private String spotAddress;
    private BigDecimal lat;
    private BigDecimal lng;
}

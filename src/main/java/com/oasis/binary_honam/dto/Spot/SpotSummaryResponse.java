package com.oasis.binary_honam.dto.Spot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpotSummaryResponse {
    private int sequenceNumber;
    private Long spotId;
    private String spotName;
    private String spotAddress;
}
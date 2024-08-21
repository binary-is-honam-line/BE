package com.oasis.binary_honam.dto.Stage;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StagePointResponse {
    private int sequenceNumber;
    private Long stageId;
    private String stageName;
    private String stageAddress;
    private BigDecimal lat;
    private BigDecimal lng;
}

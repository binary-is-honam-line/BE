package com.oasis.binary_honam.dto.Stage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StageSummaryResponse {
    private int sequenceNumber;
    private Long stageId;
    private String stageName;
    private String stageAddress;
}
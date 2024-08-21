package com.oasis.binary_honam.dto.OpenAi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageDto {
    private int sequenceNumber;
    private String stageName;
    private String stageStory;
}
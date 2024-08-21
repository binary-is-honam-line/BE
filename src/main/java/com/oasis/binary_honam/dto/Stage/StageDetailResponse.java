package com.oasis.binary_honam.dto.Stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageDetailResponse {
    private String stageName;
    private String stageAddress;
    private String stageDes;
    private String stageStory;
    private String quizContent;
    private String quizAnswer;
}
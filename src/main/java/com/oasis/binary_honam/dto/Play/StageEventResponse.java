package com.oasis.binary_honam.dto.Play;

import com.oasis.binary_honam.entity.enums.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StageEventResponse {
    private Long userStageId;
    private String stageName;
    private String stageAddress;
    private String stageStory;
    private String quizContent;
    private Answer quizAnswer;
}

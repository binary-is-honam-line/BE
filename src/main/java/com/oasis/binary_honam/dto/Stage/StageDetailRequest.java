package com.oasis.binary_honam.dto.Stage;

import com.oasis.binary_honam.entity.enums.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageDetailRequest {
    private String stageDes;
    private String stageStory;
    private String quizContent;
    private Answer quizAnswer;
}
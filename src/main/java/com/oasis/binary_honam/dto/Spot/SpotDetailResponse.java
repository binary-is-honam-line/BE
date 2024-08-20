package com.oasis.binary_honam.dto.Spot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotDetailResponse {
    private String spotName;
    private String spotAddress;
    private String spotDes;
    private String spotStory;
    private String quizContent;
    private String quizAnswer;
}
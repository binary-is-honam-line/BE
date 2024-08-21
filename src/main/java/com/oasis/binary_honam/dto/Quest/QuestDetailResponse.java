package com.oasis.binary_honam.dto.Quest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class QuestDetailResponse {
    private Long questId;
    private String questName;
    private String location;
    private String userNickname;
    private String mainStory;
    private List<String> stageNames;
}

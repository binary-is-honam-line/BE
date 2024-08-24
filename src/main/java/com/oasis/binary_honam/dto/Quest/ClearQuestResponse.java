package com.oasis.binary_honam.dto.Quest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClearQuestResponse {
    private Long questId;
    private String questName;
    private String location;
    private String userNickname;
    private String mainStory;
    private List<String> stageNames;
    private int headCount;
    private LocalTime time;
    private LocalDate date;
}

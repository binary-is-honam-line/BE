package com.oasis.binary_honam.dto.Quest;

import com.oasis.binary_honam.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class QuestSummaryResponse {
    private Long questId;
    private String questName;
    private String location;
    private String userNickname;
    private int headCount;
    private LocalTime time;
}

package com.oasis.binary_honam.dto.Quest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class SaveQuestRequest {
    private String questName;
    private String location;
    private String mainStory;
    private int headCount;
    private String time;
}

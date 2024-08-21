package com.oasis.binary_honam.dto.Quest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaveQuestRequest {
    private String questName;
    private String location;
    private String mainStory;
}

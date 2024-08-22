package com.oasis.binary_honam.dto.Quest;

import com.oasis.binary_honam.entity.Quest;
import com.oasis.binary_honam.entity.QuestAlbum;
import com.oasis.binary_honam.entity.Stage;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.entity.enums.Role;
import com.oasis.binary_honam.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class QuestDto {
    private Long questId;
    private String questName;
    private String location;
    private String mainStory;
    private String image;
    private int headCount;
    private LocalTime time;
    private Status status;
    private List<Long> stageIds;
}

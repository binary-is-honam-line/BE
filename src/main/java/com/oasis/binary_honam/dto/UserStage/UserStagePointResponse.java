package com.oasis.binary_honam.dto.UserStage;

import com.oasis.binary_honam.entity.Stage;
import com.oasis.binary_honam.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class UserStagePointResponse {
    private Long userStageId;
    private Long userId;
    private int sequenceNumber;
    private Long stageId;
    private String stageName;
    private String stageAddress;
    private BigDecimal lat;
    private BigDecimal lng;
    private boolean isCleared;
}

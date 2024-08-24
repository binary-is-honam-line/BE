package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_stage")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userStageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @Column(name = "is_cleared", nullable = false)
    private boolean isCleared;
}


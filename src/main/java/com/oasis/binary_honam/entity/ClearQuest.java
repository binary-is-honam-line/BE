package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "clear_quest")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClearQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clearQuestId;

    @Column
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_album_id", nullable = false)
    private QuestAlbum questAlbum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;
}

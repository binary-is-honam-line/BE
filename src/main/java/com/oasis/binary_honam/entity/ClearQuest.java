package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "clear_quest")
public class ClearQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clearQuestId;

    @Column
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_album_id", nullable = false)
    private QuestAlbum questAlbum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;
}

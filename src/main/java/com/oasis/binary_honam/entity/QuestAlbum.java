package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "quest_album")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questAlbumId;

    @Builder.Default
    @OneToMany(mappedBy = "questAlbum")
    private List<ClearQuest> clearQuests = new ArrayList<>();
}

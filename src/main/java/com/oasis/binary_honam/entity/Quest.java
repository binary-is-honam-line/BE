package com.oasis.binary_honam.entity;

import com.oasis.binary_honam.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "quest")
@AllArgsConstructor
@NoArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questId;

    @Column(length = 255)
    private String questName;

    @Column(length = 255)
    private String location;

    @Column
    private String mainStory;

    @Lob
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "quest")
    private List<Stage> stages = new ArrayList<>();
}

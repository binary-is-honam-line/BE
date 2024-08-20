package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "story")
@AllArgsConstructor
@NoArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storyId;

    @Column(length = 255)
    private String storyName;

    @Column(length = 255)
    private String location;

    @Column
    private String mainStory;

    @Lob
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "story")
    private List<ClearStory> clearStories = new ArrayList<>();

    @OneToMany(mappedBy = "story")
    private List<Spot> spots = new ArrayList<>();
}

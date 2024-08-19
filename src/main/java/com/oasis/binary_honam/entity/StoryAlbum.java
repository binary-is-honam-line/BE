package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "story_album")
public class StoryAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storyAlbumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "storyAlbum")
    private List<ClearStory> clearStories = new ArrayList<>();
}

package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "clear_story")
public class ClearStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clearStoryId;

    @Column
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_album_id", nullable = false)
    private StoryAlbum storyAlbum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;
}

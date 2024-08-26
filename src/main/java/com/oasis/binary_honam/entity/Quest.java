package com.oasis.binary_honam.entity;

import com.oasis.binary_honam.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "quest")
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @Column
    private int headCount;

    @Column
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "quest")
    private List<Stage> stages = new ArrayList<>();

    public void update(String questName, String location, String mainStory) {
        if (!questName.equals(this.questName))
            this.questName = questName;
        if (!location.equals(this.location))
            this.location = location;
        if (!mainStory.equals(this.mainStory))
            this.mainStory = mainStory;
        this.status = Status.SAVED;
    }

    public void imageUpdate(String newImageName) {
        this.image = newImageName;
    }

    public void statusUpdate() {
        this.status = Status.EDITING;
    }

    public void saveStory(String mainStory) {
        this.mainStory = mainStory;
    }

    public void update(String questName, String location, String mainStory, int headCount, LocalTime time) {
        if (!questName.equals(this.questName))
            this.questName = questName;
        if (!location.equals(this.location))
            this.location = location;
        if (!mainStory.equals(this.mainStory))
            this.mainStory = mainStory;
        this.status = Status.SAVED;
        this.headCount = headCount;
        this.time = time;
    }
}

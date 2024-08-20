package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "spot")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(length = 255)
    private String spotName;

    @Lob
    private String spotAddress;

    @Column(precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(precision = 9, scale = 6)
    private BigDecimal lng;

    @Lob
    private String spotStory;

    @Lob
    private String spotDes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public void update(String spotStory, String spotDes) {
        if (!spotDes.equals(this.spotDes))
            this.spotDes = spotDes;
        if (!spotStory.equals(this.spotStory))
            this.spotStory = spotStory;
    }
}


package com.oasis.binary_honam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "spot_list")
public class SpotList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spotListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @OneToMany(mappedBy = "spotList")
    private List<Spot> spots = new ArrayList<>();
}


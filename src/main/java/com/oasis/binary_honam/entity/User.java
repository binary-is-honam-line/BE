package com.oasis.binary_honam.entity;

import com.oasis.binary_honam.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String nickname;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Quest> quests = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_album_id")
    private QuestAlbum questAlbum;

    public void updatePassword(String encodePw) {
        this.password = encodePw;
    }

    public void update(String name, String nickname, String phone) {
        if (!name.equals(this.name))
            this.name = name;
        if (!nickname.equals(this.nickname))
            this.nickname = nickname;
        if (!phone.equals(this.phone))
            this.phone = phone;
    }
}

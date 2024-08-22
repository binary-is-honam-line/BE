package com.oasis.binary_honam.entity;

import com.oasis.binary_honam.entity.enums.Answer;
import com.oasis.binary_honam.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "quiz")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Answer answer;

    public void update(String content, Answer answer) {
        if (!content.equals(this.content))
            this.content = content;
        if (!answer.equals(this.answer))
            this.answer = answer;
    }
}


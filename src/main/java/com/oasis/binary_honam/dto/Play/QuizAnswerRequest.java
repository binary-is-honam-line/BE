package com.oasis.binary_honam.dto.Play;

import com.oasis.binary_honam.entity.enums.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizAnswerRequest {
    private Answer answer;
}

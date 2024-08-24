package com.oasis.binary_honam.service;

import com.oasis.binary_honam.dto.UserStage.UserStagePointResponse;
import com.oasis.binary_honam.entity.Quest;
import com.oasis.binary_honam.entity.Stage;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.entity.UserStage;
import com.oasis.binary_honam.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayService {
    private final UserRepository userRepository;
    private final StageRepository stageRepository;
    private final UserStageRepository userStageRepository;
    private final ClearQuestRepository clearQuestRepository;
    private final QuestRepository questRepository;



    public Long startPlay(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스틑를 찾을 수 없습니다: " + questId));

        // 스테이지 목록을 stageId 기준으로 정렬
        List<Stage> stages = quest.getStages().stream()
                .sorted(Comparator.comparingLong(Stage::getStageId))
                .collect(Collectors.toList());

        for (Stage stage : stages) {
            UserStage userStage = UserStage.builder()
                    .user(user)
                    .stage(stage)
                    .isCleared(false)
                    .build();
            userStageRepository.save(userStage);
        }

        return questId;
    }

    public List<UserStagePointResponse> getUserStagesPoints(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스틑를 찾을 수 없습니다: " + questId));

        // 사용자와 관련된 모든 스테이지 정보
        List<UserStage> userStages = userStageRepository.findByUser_UserId(user.getUserId());

        // 모든 스테이지와 관련된 사용자 스테이지 정보를 저장
        List<UserStagePointResponse> dtos = new ArrayList<>();

        for (int i = 0; i<userStages.size(); i++){
            Stage stage = userStages.get(i).getStage();

            UserStagePointResponse dto = UserStagePointResponse.builder()
                    .userStageId(userStages.get(i).getUserStageId())
                    .userId(user.getUserId())
                    .sequenceNumber(i+1) // 스테이지 ID를 시퀀스 번호로 사용
                    .stageId(stage.getStageId())
                    .stageName(stage.getStageName())
                    .stageAddress(stage.getStageAddress())
                    .lat(stage.getLat())
                    .lng(stage.getLng())
                    .isCleared(userStages.get(i).isCleared())
                    .build();

            dtos.add(dto);
        }

        return dtos;
    }
}

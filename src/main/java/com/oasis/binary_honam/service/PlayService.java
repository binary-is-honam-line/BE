package com.oasis.binary_honam.service;

import com.oasis.binary_honam.dto.Play.QuizAnswerRequest;
import com.oasis.binary_honam.dto.Play.StageEventResponse;
import com.oasis.binary_honam.dto.Play.UserStagePointResponse;
import com.oasis.binary_honam.entity.*;
import com.oasis.binary_honam.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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
                    .quest(quest)
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

        List<UserStage> userStages = userStageRepository.findByUser_UserIdAndQuest_QuestId(user.getUserId(), questId);

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

    public StageEventResponse getStageEvent(Long userStageId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        UserStage userStage = userStageRepository.findById(userStageId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 userStage를 찾을 수 없습니다: " + userStageId));

        // 이미 클리어된 스테이지는 반환하지 않음
        if (userStage != null && userStage.isCleared()) {
            return null; // 스테이지가 이미 클리어된 경우
        }

        Stage stage = userStage.getStage();

        StageEventResponse stageEventResponse = StageEventResponse.builder()
                .userStageId(userStage.getUserStageId())
                .stageName(stage.getStageName())
                .stageAddress(stage.getStageAddress())
                .stageStory(stage.getStageStory())
                .quizContent(stage.getQuiz().getContent())
                .quizAnswer(stage.getQuiz().getAnswer())
                .build();

        return stageEventResponse;
    }

    public boolean submitQuizAnswer(Long userStageId, QuizAnswerRequest quizAnswerRequest, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        UserStage userStage = userStageRepository.findById(userStageId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 userStage를 찾을 수 없습니다: " + userStageId));

        Stage stage = userStage.getStage();

        if (quizAnswerRequest.getAnswer().equals(stage.getQuiz().getAnswer())) {
            userStage.update(); // 스테이지 클리어 상태로 업데이트
            userStageRepository.save(userStage); // 변경 사항 저장
            return true;
        }
        return false;
    }

    public boolean isQuestCleared(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        List<UserStage> userStages = userStageRepository.findByUser_UserIdAndQuest_QuestId(user.getUserId(), questId);

        boolean allStagesCleared = true;

        for (int i = 0; i<userStages.size(); i++){
            if(!userStages.get(i).isCleared()){
                allStagesCleared = false;
                break;
            }
        }

        return allStagesCleared;
    }

    public void endPlay(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!isQuestCleared(questId, authentication)) {
            throw new IllegalStateException("퀘스트를 아직 클리어하지 않았습니다: " + questId);
        }

        // 퀘스트가 클리어된 경우 완료 처리 로직
        ClearQuest clearQuest = ClearQuest.builder()
                .date(LocalDate.now())
                .questAlbum(user.getQuestAlbum())
                .quest(quest)
                .build();

        clearQuestRepository.save(clearQuest);

        List<UserStage> userStages = userStageRepository.findByUser_UserIdAndQuest_QuestId(user.getUserId(), questId);

        userStageRepository.deleteAll(userStages);
    }
}

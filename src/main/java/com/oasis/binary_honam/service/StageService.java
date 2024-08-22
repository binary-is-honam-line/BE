package com.oasis.binary_honam.service;

import com.oasis.binary_honam.dto.Stage.*;
import com.oasis.binary_honam.entity.Quest;
import com.oasis.binary_honam.entity.Quiz;
import com.oasis.binary_honam.entity.Stage;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.entity.enums.Answer;
import com.oasis.binary_honam.repository.QuestRepository;
import com.oasis.binary_honam.repository.QuizRepository;
import com.oasis.binary_honam.repository.StageRepository;
import com.oasis.binary_honam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public List<StageSummaryResponse> getStages(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        List<StageSummaryResponse> dtos = new ArrayList<>();

        // 스테이지 목록을 stageId 기준으로 정렬
        List<Stage> stages = quest.getStages().stream()
                .sorted(Comparator.comparingLong(Stage::getStageId))
                .collect(Collectors.toList());

        for(int i = 0; i<stages.size(); i++){
            Stage stage = stages.get(i);
            dtos.add(new StageSummaryResponse(i + 1, stage.getStageId(), stage.getStageName(), stage.getStageAddress()));
        }

        return dtos;
    }

    public void createStage(Long questId, StageCreateRequest stageCreateRequest, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        String stageDes = stageCreateRequest.getStageDes() != null ? stageCreateRequest.getStageDes() : "";

        // 새로운 Quiz 생성
        Quiz quiz = Quiz.builder()
                .content("")
                .answer(Answer.N)
                .build();
        quizRepository.save(quiz);

        Stage stage = Stage.builder()
                .quest(quest)
                .stageName(stageCreateRequest.getStageName())
                .stageAddress(stageCreateRequest.getStageAddress())
                .lat(stageCreateRequest.getLat())
                .lng(stageCreateRequest.getLng())
                .stageDes(stageDes)
                .quiz(quiz)
                .stageStory("")
                .build();
        stageRepository.save(stage);

        System.out.println(stageCreateRequest.getStageAddress());
    }

    public StageDetailResponse getStageDetail(Long stageId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 스테이지를 찾을 수 없습니다: " + stageId));

        if (!stage.getQuest().getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        StageDetailResponse dto = StageDetailResponse.builder()
                .stageName(stage.getStageName())
                .stageAddress(stage.getStageAddress())
                .stageStory(stage.getStageStory())
                .stageDes(stage.getStageDes())
                .quizContent(stage.getQuiz().getContent())
                .quizAnswer(stage.getQuiz().getAnswer())
                .build();

        return dto;
    }

    public void updateStageDetail(Long stageId, StageDetailRequest stageDetailRequest, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 장소를 찾을 수 없습니다: " + stageId));

        if (!stage.getQuest().getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        Quiz quiz = quizRepository.findById(stage.getQuiz().getQuizId())
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀴즈를 찾을 수 없습니다: " + stage.getQuiz().getQuizId()));

        String stageStory = stageDetailRequest.getStageStory() != null ? stageDetailRequest.getStageStory() : "";
        String stageDes = stageDetailRequest.getStageDes() != null ? stageDetailRequest.getStageDes() : "";
        String content = stageDetailRequest.getQuizContent() != null ? stageDetailRequest.getQuizContent() : "";
        Answer answer = stageDetailRequest.getQuizAnswer() != null ? stageDetailRequest.getQuizAnswer() : Answer.N;

        if (content.equals(""))
            answer = Answer.N;
        if (answer.equals(Answer.N))
            content = "";

        stage.update(stageStory, stageDes);
        quiz.update(content, answer);

        stageRepository.save(stage);
        quizRepository.save(quiz);
    }

    public void deleteStage(Long stageId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 스테이지를 찾을 수 없습니다: " + stageId));

        if (!stage.getQuest().getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        stageRepository.deleteById(stageId);
        quizRepository.deleteById(stage.getQuiz().getQuizId());
    }

    public List<StagePointResponse> getStagesPoints(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스틑를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        List<StagePointResponse> dtos = new ArrayList<>();

        List<Stage> stages = quest.getStages().stream()
                .sorted(Comparator.comparingLong(Stage::getStageId))
                .collect(Collectors.toList());

        for(int i = 0; i<stages.size(); i++){
            Stage stage = stages.get(i);
            dtos.add(new StagePointResponse(i + 1, stage.getStageId(), stage.getStageName(), stage.getStageAddress(), stage.getLat(), stage.getLng()));
        }

        return dtos;
    }
}

package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.Play.QuizAnswerRequest;
import com.oasis.binary_honam.dto.Play.UserLocation;
import com.oasis.binary_honam.dto.Quest.QuestDetailResponse;
import com.oasis.binary_honam.dto.Play.StageEventResponse;
import com.oasis.binary_honam.dto.Play.UserStagePointResponse;
import com.oasis.binary_honam.service.PlayService;
import com.oasis.binary_honam.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "플레이")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/play")
public class PlayController {
    private final PlayService playService;
    private final QuestService questService;

    @GetMapping("/{questId}/detail")
    @Operation(summary = "퀘스트 상세 조회하기")
    public QuestDetailResponse getQuestDetail(@PathVariable Long questId,
                                              Authentication authentication){
        return questService.getQuestDetail(questId, authentication);
    }

    @GetMapping(value = "/{questId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "퀘스트 이미지 조회하기")
    public Resource getQuestImage(@PathVariable Long questId,
                                  Authentication authentication) {
        return questService.getQuestImage(questId, authentication);
    }

    @GetMapping("/{questId}/start")
    @Operation(summary = "플레이 시작")
    public Long startPlay(@PathVariable Long questId,
                          Authentication authentication){
        return playService.startPlay(questId, authentication);
    }

    @GetMapping("/{questId}/points")
    @Operation(summary = "스테이지 좌표 목록 전체 조회")
    public List<UserStagePointResponse> getUserStagesPoints(@PathVariable Long questId,
                                                            Authentication authentication){
        return playService.getUserStagesPoints(questId, authentication);
    }

    @GetMapping("/{questId}/{userStageId}")
    @Operation(summary = "이벤트 조회")
    public ResponseEntity<StageEventResponse> getStageEvent(@PathVariable Long questId,
                                                            @PathVariable Long userStageId,
                                                            UserLocation userLocation,
                                                            Authentication authentication) {
        if (playService.checkProximity(userStageId, userLocation)){
            StageEventResponse response = playService.getStageEvent(userStageId, authentication);
            if (response == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 스테이지가 이미 클리어된 경우
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 스테이지 근처가 아닌 경우
        }
    }

    @PostMapping("/{questId}/{userStageId}")
    @Operation(summary = "퀴즈 풀기")
    public ResponseEntity<String> submitQuizAnswer(@PathVariable Long questId,
                                                   @PathVariable Long userStageId,
                                                   @RequestBody QuizAnswerRequest quizAnswerRequest,
                                                   Authentication authentication) {
        boolean isCleared = playService.submitQuizAnswer(userStageId, quizAnswerRequest, authentication);
        if (isCleared) {
            return new ResponseEntity<>("스테이지를 클리어 했습니다!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("다시 풀어보세요!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/quest/{questId}")
    @Operation(summary = "퀘스트 클리어 유무")
    public ResponseEntity<String> getQuestClearStatus(@PathVariable Long questId,
                                                      Authentication authentication) {
        boolean isCleared = playService.isQuestCleared(questId, authentication);
        if (isCleared) {
            return new ResponseEntity<>("퀘스트를 모두 클리어했습니다!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("퀘스트를 아직 클리어하지 않았습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{questId}/end")
    @Operation(summary = "플레이 종료")
    public ResponseEntity endPlay(@PathVariable Long questId,
                                  Authentication authentication) {
        playService.endPlay(questId, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }
}

package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.Quest.QuestDetailResponse;
import com.oasis.binary_honam.dto.UserStage.UserStagePointResponse;
import com.oasis.binary_honam.service.PlayService;
import com.oasis.binary_honam.service.QuestService;
import com.oasis.binary_honam.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "플레이")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/play")
public class PlayController {
    private final PlayService playService;
    private final StageService stageService;
    private final QuestService questService;

    @GetMapping("/{questId}")
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
    @Operation(summary = "플레이 버튼을 눌렀을때")
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
}

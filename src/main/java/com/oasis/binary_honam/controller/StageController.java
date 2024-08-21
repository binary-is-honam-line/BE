package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.Spot.*;
import com.oasis.binary_honam.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "스테이지")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stages")
public class StageController {
    private final StageService stageService;

    @GetMapping("/{questId}")
    @Operation(summary = "스테이지 목록 전체 조회")
    public List<StageSummaryResponse> getStages(Long questId,
                                              Authentication authentication){
        return stageService.getStages(questId, authentication);
    }

    @PostMapping("/{questId}/create")
    @Operation(summary = "스테이지 추가하기")
    public ResponseEntity createStage(Long questId,
                                     StageCreateRequest stageCreateRequest,
                                     Authentication authentication){
        stageService.createStage(questId, stageCreateRequest, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{questId}/{stageId}")
    @Operation(summary = "스테이지 상세 조회하기")
    public StageDetailResponse getStageDetail(Long stageId,
                                            Authentication authentication){
        return stageService.getStageDetail(stageId, authentication);
    }

    @PutMapping("/{questId}/{stageId}")
    @Operation(summary = "스테이지 상세 수정하기")
    public ResponseEntity updateStageDetail(Long stageId,
                                           StageDetailRequest stageDetailRequest,
                                           Authentication authentication){
        stageService.updateStageDetail(stageId, stageDetailRequest, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{questId}/{stageId}")
    @Operation(summary = "스테이지 삭제하기")
    public ResponseEntity deleteStage(Long stageId,
                                     Authentication authentication){
        stageService.deleteStage(stageId, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{stageId}/points")
    @Operation(summary = "스테이지 좌표 목록 전체 조회")
    public List<StagePointResponse> getStagesPoints(Long questId,
                                                  Authentication authentication){
        return stageService.getStagesPoints(questId, authentication);
    }
}

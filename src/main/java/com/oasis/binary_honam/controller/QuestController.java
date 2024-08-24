package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.Quest.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "퀘스트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quests")
public class QuestController {
    private final QuestService questService;

    // 퀘스트 임시 생성
    // status: editing
    @PostMapping("/create")
    @Operation(summary = "퀘스트 생성")
    public CreateQuestResponse createQuest(Authentication authentication){
        return questService.createQuest(authentication);
    }

    // 퀘스트 최종 저장, 수정 완료
    // status: saved
    @PostMapping("/{questId}/save")
    @Operation(summary = "퀘스트 저장, 수정")
    public ResponseEntity saveQuest(SaveQuestRequest saveQuestRequest,
                                    @PathVariable Long questId,
                                    Authentication authentication){
        questService.saveQuest(saveQuestRequest, questId, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{questId}/update")
    @Operation(summary = "퀘스트 수정하기 버튼을 눌렀을때")
    public QuestDto updateQuest(@PathVariable Long questId,
                                Authentication authentication){
        return questService.updateQuest(questId, authentication);
    }

    @GetMapping(value = "/{questId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "퀘스트 이미지 조회하기")
    public Resource getQuestImage(@PathVariable Long questId,
                                  Authentication authentication) {
        return questService.getQuestImage(questId, authentication);
    }

    @PostMapping(value = "/{questId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "퀘스트 이미지 저장, 수정")
    public ResponseEntity saveQuestImage(@RequestParam MultipartFile file,
                                         @PathVariable Long questId,
                                         Authentication authentication) {
        questService.saveQuestImage(file, questId, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    // 전체 간략 조회
    @GetMapping("/")
    @Operation(summary = "퀘스트 목록 전체 조회")
    public List<QuestSummaryResponse> getQuests(Authentication authentication){
        return questService.getQuests(authentication);
    }

    @DeleteMapping("/{questId}")
    @Operation(summary = "퀘스트 삭제하기")
    public ResponseEntity deleteQuest(@PathVariable Long questId,
                                      Authentication authentication){
        questService.deleteQuest(questId, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }
}

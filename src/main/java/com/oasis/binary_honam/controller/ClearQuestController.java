package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.Quest.ClearQuestResponse;
import com.oasis.binary_honam.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "클리어한 퀘스트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clear")
public class ClearQuestController {
    private final QuestService questService;

    @GetMapping("/quest-album")
    @Operation(summary = "클리어한 퀘스트 조회")
    public List<ClearQuestResponse> getClearQuest(Authentication authentication){
        return questService.getClearQuest(authentication);
    }

    @GetMapping("/quest-album/count")
    @Operation(summary = "클리어한 퀘스트 갯수 조회")
    public int getClearQuestCnt(Authentication authentication){
        return getClearQuest(authentication).size();
    }
}

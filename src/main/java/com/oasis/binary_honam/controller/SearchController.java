package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.Quest.QuestSummaryResponse;
import com.oasis.binary_honam.dto.Quest.SearchQuestRequest;
import com.oasis.binary_honam.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "검색")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
    private final QuestService questService;

    @GetMapping("/suggest")
    @Operation(summary = "추천 퀘스트 5개 랜덤 제공")
    public List<QuestSummaryResponse> suggestQuest(Authentication authentication){
        return questService.suggestQuest(authentication);
    }

    @GetMapping("/")
    @Operation(summary = "지역별, 퀘스트명별 검색")
    public List<QuestSummaryResponse> searchQuest(SearchQuestRequest searchQuestRequest,
                                                  Authentication authentication){
        return questService.searchQuest(searchQuestRequest, authentication);
    }
}

package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.OpenAi.StoryDto;
import com.oasis.binary_honam.service.AIStoryTellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI 스토리 생성 봇")
@RestController
@RequestMapping("/api/story-teller")
@RequiredArgsConstructor
public class AIStoryTellerController {
    private final AIStoryTellerService aiStoryTellerService;

    @PostMapping("{questId}/story")
    @Operation(summary = "스토리 생성")
    public void createStory(@RequestParam(name = "input")String input,
                              @PathVariable Long questId,
                              Authentication authentication){
        aiStoryTellerService.createStory(input, questId, authentication);
    }

    @GetMapping("{questId}/story")
    @Operation(summary = "스토리 조회")
    public StoryDto getStory(@PathVariable Long questId,
                             Authentication authentication){
        return aiStoryTellerService.getStory(questId, authentication);
    }
}

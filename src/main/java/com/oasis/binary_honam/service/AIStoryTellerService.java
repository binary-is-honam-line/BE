package com.oasis.binary_honam.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oasis.binary_honam.dto.OpenAi.*;
import com.oasis.binary_honam.entity.Quest;
import com.oasis.binary_honam.entity.Stage;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.repository.QuestRepository;
import com.oasis.binary_honam.repository.StageRepository;
import com.oasis.binary_honam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIStoryTellerService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    private final RestTemplate template;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final StageRepository stageRepository;

    public String createStory(String input, Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        String stageNames = "";

        // 스테이지 목록을 stageId 기준으로 정렬
        List<Stage> stages = quest.getStages().stream()
                .sorted(Comparator.comparingLong(Stage::getStageId))
                .collect(Collectors.toList());

        for (int i = 0; i<quest.getStages().size(); i++){
            stageNames += stages.get(i).getStageName() + "\n";
        }

        String prompt = "사용자의 입력과 스테이지별로 스테이지의 이름을 바탕으로 스테이지별 스토리를 작성해주세요. 각 스테이지 스토리는 스테이지의 이름에 대한 정보를 토대로 게임의 스테이지로 상상하여 몰입감 있게 작성하는데, \"~번째 스테이지인 ~~입니다. 이곳은 ~~\" 이런 식으로 작성해주세요. 메인 스토리는 스테이지의 스토리들을 반영해서 스테이지를 클리어 하지않은 사용자가 클리어를 하였을때 느낄 수 있는 기분, 얻을 수 있는 것, 체험할 수 있는 것, 깨달음 등으로 작성해주세요. 모든 스토리는 앞으로 발생할 일에 대한 설명이기 때문에 과거형이 아닌 \"~~할 것입니다.\" 를 사용해야 합니다. 메인 스토리는 하나이고, 스테이지 스토리는 주어진 스테이지 리스트의 스테이지 갯수만큼 생성되어야 합니다. 각 스테이지의 Number은 Long 타입, 스테이지 이름과 스테이지 스토리, 메인 스토리는 모두 String 타입입니다. 스테이지별 이름은 \\n을 기준으로 나눠져 있습니다. 또한 존댓말을 사용해주세요. 대답 형식은 스테이지의 갯수에 따라 변화할 수 있습니다.\n\n"
                + "사용자 입력은 다음과 같습니다: " + input + "\n"
                + "장소별 이름은 다음과 같습니다: " + stageNames + "\n"
                + "대답 형식은 다음과 같습니다:\n"
                + "{\n"
                + "    \"stage\":\n"
                + "    [\n"
                + "        {\"sequenceNumber\": 1, \"stageName\": \"스테이지 이름\", \"stageStory\": \"스테이지 스토리\"},\n"
                + "        {\"sequenceNumber\": 2, \"stageName\": \"다음 스테이지 이름\", \"stageStory\": \"다음 스테이지 스토리\"},\n"
                + "        {\"sequenceNumber\": 3, \"stageName\": \"다음 스테이지 이름\", \"stageStory\": \"다음 스테이지 스토리\"}\n"
                + "    ],\n"
                + "    \"main\":\n"
                + "    {\"mainStory\": \"메인 스토리\"}\n"
                + "}\n";

        try {
            ChatGPTRequest request = new ChatGPTRequest(model, prompt);
            ChatGPTResponse chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
            if (chatGPTResponse != null && !chatGPTResponse.getChoices().isEmpty()) {
                String jsonString = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                parser(jsonString, questId, authentication);
                return jsonString;
            } else {
                throw new RuntimeException("ChatGPT 응답이 비어 있습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 로그에 오류 출력
            throw new RuntimeException("ChatGPT API 호출 중 오류가 발생했습니다.", e);
        }
    }

    public void parser(String jsonString, Long questId, Authentication authentication){
        ObjectMapper mapper = new ObjectMapper();

        try {
            // JSON 문자열을 JsonNode로 파싱
            JsonNode rootNode = mapper.readTree(jsonString);

            // StageDto 리스트 추출
            JsonNode stagesNode = rootNode.get("stage");
            List<StageDto> stageDtoList = new ArrayList<>();
            for (JsonNode stageNode : stagesNode) {
                StageDto stageDto = mapper.treeToValue(stageNode, StageDto.class);
                stageDtoList.add(stageDto);
            }

            // MainDto 추출
            JsonNode mainNode = rootNode.get("main");
            MainDto mainDto = mapper.treeToValue(mainNode, MainDto.class);

            // DTO 객체에 접근
            System.out.println("Stage DTOs:");
            for (StageDto stageDto : stageDtoList) {
                System.out.println("Stage Number: " + stageDto.getSequenceNumber());
                System.out.println("Stage Name: " + stageDto.getStageName());
                System.out.println("Stage Story: " + stageDto.getStageStory());
            }

            System.out.println("Main DTO:");
            System.out.println("Main Story: " + mainDto.getMainStory());

            saveStory(stageDtoList, mainDto, questId, authentication);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveStory(List<StageDto> stageDtoList, MainDto mainDto, Long questId, Authentication authentication){
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        // 스테이지 목록을 stageId 기준으로 정렬
        List<Stage> stages = quest.getStages().stream()
                .sorted(Comparator.comparingLong(Stage::getStageId))
                .collect(Collectors.toList());

        for(int i = 0; i<stages.size(); i++){
            Stage stage = stages.get(i);
            stage.saveStory(stageDtoList.get(i).getStageStory());
            stageRepository.save(stage);
        }

        quest.saveStory(mainDto.getMainStory());
        questRepository.save(quest);
    }

    public StoryDto getStory(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        // 스테이지 목록을 stageId 기준으로 정렬
        List<Stage> stages = quest.getStages().stream()
                .sorted(Comparator.comparingLong(Stage::getStageId))
                .collect(Collectors.toList());

        MainDto mainDto = MainDto.builder()
                .mainStory(quest.getMainStory())
                .build();

        List<StageDto> stageDtoList = new ArrayList<>();

        for (int i = 0; i<stages.size(); i++){
            StageDto stageDto = StageDto.builder()
                    .sequenceNumber(i+1)
                    .stageStory(stages.get(i).getStageStory())
                    .stageName(stages.get(i).getStageName())
                    .build();
            stageDtoList.add(stageDto);
        }

        StoryDto storyDto = StoryDto.builder()
                .stageDtoList(stageDtoList)
                .mainDto(mainDto)
                .build();

        return storyDto;
    }
}

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

        String prompt = "사용자의 입력과 스테이지별로 스테이지의 이름을 바탕으로 스테이지별 스토리를 작성해주세요. 각 스테이지 스토리는 게임의 스테이지로 상상하여 몰입감 있게 설명하고, 후에 사용자의 입력을 반영하여 전체 메인 스토리를 작성해주세요. 메인 스토리는 하나이고, 장소 스토리는 주어진 장소 리스트의 장소 갯수만큼 생성되어야 합니다. 각 스테이지의 Number은 Long 타입, 스테이지 이름과 스테이지 스토리, 메인 스토리는 모두 String 타입입니다. 장소별 이름은 \\n을 기준으로 나눠져 있습니다. 또한 존댓말을 사용해주세요. 스토리는 앞으로 발생할 일에 대해 설명하기 때문에 과거형을 사용하지 말아주세요.\n\n"
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
                + "}\n"
                + "대답 형식 예시는 다음과 같습니다:\n"
                + "{\n"
                + "    \"stage\":\n"
                + "    [\n"
                + "        {\"sequenceNumber\": 1, \"stageName\": \"광주사직공원\", \"stageStory\": \"당신의 모험이 시작되는 곳은 광주사직공원입니다. 이곳은 자연과 평화가 어우러지는 힐링의 장소로, 펼쳐진 연못과 푸른 잔디가 당신을 맞이합니다. 공원 중앙의 연못은 다양한 조류와 함께 고요한 분위기를 자아내며, 산책로를 따라 펼쳐진 풍경은 여유롭고 평화로운 시간을 선사합니다. 이곳에서 첫 번째 스테이지를 완수하며 편안한 마음으로 다음 여정을 준비하세요.\"},\n"
                + "        {\"sequenceNumber\": 2, \"stageName\": \"광주호수공원\", \"stageStory\": \"두 번째 스테이지, 광주호수공원으로 향합니다. 이곳은 광주 지역의 대표적인 야외 명소로, 넓고 잔잔한 호수와 잘 조성된 산책로가 매력적입니다. 호수 주변을 따라 걸으며 경쾌한 물소리와 자연의 아름다움을 경험하세요. 자전거를 타고 호수를 한 바퀴 도는 것도 좋은 선택이 될 것입니다. 이 스테이지에서 신선한 공기와 활력을 얻고, 다음 목표를 향해 나아가세요.\"},\n"
                + "        {\"sequenceNumber\": 3, \"stageName\": \"광주민속박물관\", \"stageStory\": \"모험의 마지막 스테이지는 광주민속박물관입니다. 이곳에서 광주의 깊은 역사와 전통을 탐험하세요. 박물관의 다양한 전시와 문화 유물들은 한국의 전통 생활과 역사를 생생하게 전해줍니다. 각 전시물과 설명을 통해 광주의 전통과 문화를 이해하며, 여행의 마지막을 의미 있게 마무리하세요.\"}\n"
                + "    ],\n"
                + "    \"main\":\n"
                + "    {\"mainStory\": \"모험이 시작된 광주사직공원에서의 평화로운 시작을 뒤로하고, 광주호수공원에서의 활력 넘치는 산책을 거쳐, 마지막으로 광주민속박물관에서의 문화 탐방으로 여정을 마무리합니다. 각 스테이지에서의 특별한 순간들을 통해 여행지를 더 깊이 이해하고, 잊지 못할 추억을 만들어보세요.\"}\n"
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

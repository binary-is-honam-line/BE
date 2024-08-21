package com.oasis.binary_honam.service;

import com.oasis.binary_honam.dto.Quest.*;
import com.oasis.binary_honam.entity.Quest;
import com.oasis.binary_honam.entity.Stage;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.entity.enums.Status;
import com.oasis.binary_honam.repository.QuestRepository;
import com.oasis.binary_honam.repository.QuizRepository;
import com.oasis.binary_honam.repository.StageRepository;
import com.oasis.binary_honam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final StageRepository stageRepository;

    @Value("${file}")
    private String rootFilePath;

    @Value("${default.image.path}")
    private String defaultImagePath;

    // 퀘스트 임시 생성
    // status: editing
    public CreateQuestResponse createQuest(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        String newImageName = saveDefaultImage();

        Quest quest = Quest.builder()
                .questName("")
                .location("")
                .mainStory("")
                .user(user)
                .status(Status.EDITING)
                .image(newImageName)
                .build();

        questRepository.save(quest);

        return new CreateQuestResponse(quest.getQuestId());
    }

    private String saveDefaultImage() {
        try {
            // 기본 이미지 파일을 ClassPath에서 가져옴
            ClassPathResource defaultImageResource = new ClassPathResource(defaultImagePath);

            // 복사할 파일의 새로운 이름 생성
            String newImageName = UUID.randomUUID().toString() + "_default_image.jpg";
            Path newImagePath = Paths.get(rootFilePath, newImageName);

            // 경로에 필요한 디렉토리 생성
            Files.createDirectories(newImagePath.getParent());

            // 기본 이미지를 새 경로로 복사
            Files.copy(defaultImageResource.getInputStream(), newImagePath);

            return newImageName;
        } catch (IOException e) {
            throw new RuntimeException("기본 이미지를 저장하는 동안 오류가 발생했습니다.", e);
        }
    }

    public Resource getQuestImage(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        try {
            Path path = Paths.get(rootFilePath, quest.getImage()).toAbsolutePath();
            Resource imageResource = new UrlResource(path.toUri());

            if (imageResource.exists() && imageResource.isReadable()) {
                return imageResource;
            } else {
                throw new IOException("이미지를 찾을 수 없거나 읽을 수 없습니다.");
            }

        } catch (IOException e) {
            throw new RuntimeException("이미지를 로드하는 동안 오류가 발생했습니다.", e);
        }
    }

    // 퀘스트 최종 저장, 수정 완료
    // status: saved
    public void saveQuest(SaveQuestRequest saveQuestRequest, Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        String questName = saveQuestRequest.getQuestName() != null ? saveQuestRequest.getQuestName() : "";
        String location = saveQuestRequest.getLocation() != null ? saveQuestRequest.getLocation() : "";
        String mainStory = saveQuestRequest.getMainStory() != null ? saveQuestRequest.getMainStory() : "";

        quest.update(questName, location, mainStory);

        questRepository.save(quest);
    }

    public List<QuestSummaryResponse> getQuests(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        List<Quest> quests = user.getQuests();

        List<QuestSummaryResponse> dtos = new ArrayList<>();

        for(int i = 0; i<quests.size(); i++){
            Quest quest = quests.get(i);
            dtos.add(new QuestSummaryResponse(quest.getQuestId(), quest.getQuestName(), quest.getLocation(), quest.getUser().getNickname()));
        }

        return dtos;
    }

    public QuestDetailResponse getQuestDetail(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        List<String> stageNames = new ArrayList<>();

        for (int i = 0; i<quest.getStages().size(); i++){
            stageNames.add(quest.getStages().get(i).getStageName());
        }

        QuestDetailResponse dto = QuestDetailResponse.builder()
                .questId(quest.getQuestId())
                .questName(quest.getQuestName())
                .location(quest.getLocation())
                .userNickname(quest.getUser().getNickname())
                .mainStory(quest.getMainStory())
                .stageNames(stageNames)
                .build();

        return dto;
    }

    public void deleteQuest(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        List<Stage> stages = quest.getStages();

        for (int i = 0; i<stages.size(); i++){
            stageRepository.deleteById(stages.get(i).getStageId());
            quizRepository.deleteById(stages.get(i).getQuiz().getQuizId());
        }

        questRepository.deleteById(questId);
    }

    public void saveQuestImage(MultipartFile file, Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }
        try {
            // 기존 이미지 파일 삭제
            if (StringUtils.hasText(quest.getImage())) {
                Path oldPath = Paths.get(rootFilePath, quest.getImage()).toAbsolutePath();
                Files.deleteIfExists(oldPath);
            }

            // 새로운 이미지 파일 저장
            String newImageName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path newPath = Paths.get(rootFilePath, newImageName).toAbsolutePath();
            Files.createDirectories(newPath.getParent());
            file.transferTo(newPath.toFile());

            // Quest 엔티티의 image 업데이트
            quest.imageUpdate(newImageName);

            // DB에 저장
            questRepository.save(quest);

        } catch (IOException e) {
            throw new RuntimeException("이미지를 업데이트하는 동안 오류가 발생했습니다.", e);
        }
    }

    public QuestDto updateQuest(Long questId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀘스트를 찾을 수 없습니다: " + questId));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        quest.statusUpdate();

        List<Long> stageIds = new ArrayList<>();

        for (int i = 0; i<quest.getStages().size(); i++){
            stageIds.add(quest.getStages().get(i).getStageId());
        }

        QuestDto dto = QuestDto.builder()
                .questId(quest.getQuestId())
                .questName(quest.getQuestName())
                .location(quest.getLocation())
                .mainStory(quest.getMainStory())
                .image(quest.getImage())
                .status(quest.getStatus())
                .stageIds(stageIds)
                .build();

        return dto;
    }
}

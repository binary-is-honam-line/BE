package com.oasis.binary_honam.service;

import com.oasis.binary_honam.dto.Spot.SpotCreateRequest;
import com.oasis.binary_honam.dto.Spot.SpotDetailRequest;
import com.oasis.binary_honam.dto.Spot.SpotDetailResponse;
import com.oasis.binary_honam.dto.Spot.SpotSummaryResponse;
import com.oasis.binary_honam.entity.Quiz;
import com.oasis.binary_honam.entity.Spot;
import com.oasis.binary_honam.entity.Story;
import com.oasis.binary_honam.entity.User;
import com.oasis.binary_honam.repository.QuizRepository;
import com.oasis.binary_honam.repository.SpotRepository;
import com.oasis.binary_honam.repository.StoryRepository;
import com.oasis.binary_honam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotService {
    private final SpotRepository spotRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public List<SpotSummaryResponse> getSpots(Long storyId, Authentication authentication) {
        // 사용자 인증 처리
        User user = (User) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        // storyId가 유효하지 않을 때 처리
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 스토리를 찾을 수 없습니다: " + storyId));

        if (!story.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        List<SpotSummaryResponse> dtos = new ArrayList<>();

        // 장소 목록을 spotId 기준으로 정렬
        List<Spot> spots = story.getSpots().stream()
                .sorted(Comparator.comparingLong(Spot::getSpotId))
                .collect(Collectors.toList());

        for(int i = 0; i<spots.size(); i++){
            Spot spot = spots.get(i);
            dtos.add(new SpotSummaryResponse(i + 1, spot.getSpotId(), spot.getSpotName(), spot.getSpotAddress()));
        }

        return dtos;
    }

    public void createSpot(Long storyId, SpotCreateRequest spotCreateRequest, Authentication authentication) {
        // 사용자 인증 처리
        User user = (User) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        // storyId가 유효하지 않을 때 처리
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 스토리를 찾을 수 없습니다: " + storyId));

        if (!story.getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        String spotDes = spotCreateRequest.getSpotDes() != null ? spotCreateRequest.getSpotDes() : "";

        // 새로운 Quiz 생성
        Quiz quiz = Quiz.builder()
                .content("")
                .answer("")
                .build();
        quizRepository.save(quiz);

        Spot spot = Spot.builder()
                .story(story)
                .spotName(spotCreateRequest.getSpotName())
                .spotAddress(spotCreateRequest.getSpotAddress())
                .lat(spotCreateRequest.getLat())
                .lng(spotCreateRequest.getLng())
                .spotDes(spotDes)
                .quiz(quiz)
                .spotStory("")
                .build();
        spotRepository.save(spot);

        System.out.println(spotCreateRequest.getSpotAddress());
    }

    public SpotDetailResponse getSpotDetail(Long spotId, Authentication authentication) {
        // 사용자 인증 처리
        User user = (User) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 장소를 찾을 수 없습니다: " + spotId));

        if (!spot.getStory().getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        SpotDetailResponse dto = SpotDetailResponse.builder()
                .spotName(spot.getSpotName())
                .spotAddress(spot.getSpotAddress())
                .spotStory(spot.getSpotStory())
                .spotDes(spot.getSpotDes())
                .quizContent(spot.getQuiz().getContent())
                .quizAnswer(spot.getQuiz().getAnswer())
                .build();

        return dto;
    }

    public void updateSpotDetail(Long spotId, SpotDetailRequest spotDetailRequest, Authentication authentication) {
        // 사용자 인증 처리
        User user = (User) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 장소를 찾을 수 없습니다: " + spotId));

        if (!spot.getStory().getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        Quiz quiz = quizRepository.findById(spot.getQuiz().getQuizId())
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 퀴즈를 찾을 수 없습니다: " + spot.getQuiz().getQuizId()));

        String spotStory = spotDetailRequest.getSpotStory() != null ? spotDetailRequest.getSpotStory() : "";
        String spotDes = spotDetailRequest.getSpotDes() != null ? spotDetailRequest.getSpotDes() : "";
        String content = spotDetailRequest.getQuizContent() != null ? spotDetailRequest.getQuizContent() : "";
        String answer = spotDetailRequest.getQuizAnswer() != null ? spotDetailRequest.getQuizAnswer() : "";

        if (content.equals(""))
            answer = "";
        if (answer.equals(""))
            content = "";

        spot.update(spotStory, spotDes);
        quiz.update(content, answer);

        spotRepository.save(spot);
        quizRepository.save(quiz);
    }

    public void deleteSpot(Long spotId, Authentication authentication) {
        // 사용자 인증 처리
        User user = (User) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 장소를 찾을 수 없습니다: " + spotId));

        if (!spot.getStory().getUser().equals(user)) {
            throw new AccessDeniedException("해당 경로에 접근할 권한이 없습니다.");
        }

        spotRepository.deleteById(spotId);
    }
}

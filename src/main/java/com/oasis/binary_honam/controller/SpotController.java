package com.oasis.binary_honam.controller;

import com.oasis.binary_honam.dto.Spot.*;
import com.oasis.binary_honam.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "장소")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots")
public class SpotController {
    private final SpotService spotService;

    @GetMapping("/{storyId}")
    @Operation(summary = "장소 목록 전체 조회")
    public List<SpotSummaryResponse> getSpots(Long storyId,
                                              Authentication authentication){
        return spotService.getSpots(storyId, authentication);
    }

    @PostMapping("/{storyId}/create")
    @Operation(summary = "장소 추가하기")
    public ResponseEntity createSpot(Long storyId,
                                     SpotCreateRequest spotCreateRequest,
                                     Authentication authentication){
        spotService.createSpot(storyId, spotCreateRequest, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{storyId}/{spotId}")
    @Operation(summary = "장소 상세 조회하기")
    public SpotDetailResponse getSpotDetail(Long spotId,
                                            Authentication authentication){
        return spotService.getSpotDetail(spotId, authentication);
    }

    @PatchMapping("/{storyId}/{spotId}")
    @Operation(summary = "장소 상세 수정하기")
    public ResponseEntity updateSpotDetail(Long spotId,
                                           SpotDetailRequest spotDetailRequest,
                                           Authentication authentication){
        spotService.updateSpotDetail(spotId, spotDetailRequest, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{storyId}/{spotId}")
    @Operation(summary = "장소 삭제하기")
    public ResponseEntity deleteSpot(Long spotId,
                                     Authentication authentication){
        spotService.deleteSpot(spotId, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{storyId}/points")
    @Operation(summary = "장소 좌표 목록 전체 조회")
    public List<SpotPointResponse> getSpotsPoints(Long storyId,
                                                  Authentication authentication){
        return spotService.getSpotsPoints(storyId, authentication);
    }
}

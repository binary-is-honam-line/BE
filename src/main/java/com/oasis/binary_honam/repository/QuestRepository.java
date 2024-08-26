package com.oasis.binary_honam.repository;

import com.oasis.binary_honam.entity.Quest;
import com.oasis.binary_honam.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    // 랜덤하게 5개의 SAVED 상태의 퀘스트 가져오기
    @Query(value = "SELECT * FROM quest WHERE status = :status ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Quest> findRandomQuestsByStatus(@Param("status") String status, @Param("limit") int limit);

    // 키워드와 위치를 사용하여 SAVED 상태의 퀘스트 검색
    List<Quest> findByQuestNameContainingAndLocationContainingAndStatus(String questName, String location, Status status);

    // 위치로만 SAVED 상태의 퀘스트 검색
    List<Quest> findByLocationContainingAndStatus(String location, Status status);

    // 키워드로만 SAVED 상태의 퀘스트 검색
    List<Quest> findByQuestNameContainingAndStatus(String questName, Status status);

    // 모든 SAVED 상태의 퀘스트 검색
    List<Quest> findByStatus(Status status);

    // 전남, 전북 선택 했을때 퀘스트 검색
    // 키워드: 특정 키워드 / 위치: 전남, 전북 / 상태: SAVED
    @Query("SELECT q FROM Quest q WHERE q.status = :status AND q.questName LIKE %:keyword% AND q.location IN (:locations)")
    List<Quest> findByQuestNameContainingAndLocationInAndStatus(
            @Param("keyword") String keyword,
            @Param("locations") List<String> locations,
            @Param("status") Status status
    );

    // 키워드: 전체 / 위치: 전남, 전북 / 상태: SAVED
    @Query("SELECT q FROM Quest q WHERE q.status = :status AND q.location IN (:locations)")
    List<Quest> findByLocationsContainingAndStatus(
            @Param("locations") List<String> locations,
            @Param("status") Status status
    );
}

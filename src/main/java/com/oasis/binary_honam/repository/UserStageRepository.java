package com.oasis.binary_honam.repository;

import com.oasis.binary_honam.entity.UserStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStageRepository extends JpaRepository<UserStage, Long> {

    List<UserStage> findByUser_UserIdAndQuest_QuestId(Long userId, Long questId);
}

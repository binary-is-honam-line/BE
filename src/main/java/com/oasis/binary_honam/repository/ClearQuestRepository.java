package com.oasis.binary_honam.repository;

import com.oasis.binary_honam.dto.Quest.ClearQuestResponse;
import com.oasis.binary_honam.entity.ClearQuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClearQuestRepository extends JpaRepository<ClearQuest, Long> {
    List<ClearQuest> findByQuestAlbum_QuestAlbumId(Long questAlbumId);
}

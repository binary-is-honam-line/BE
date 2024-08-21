package com.oasis.binary_honam.repository;

import com.oasis.binary_honam.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {
}

package com.oasis.binary_honam.repository;

import com.oasis.binary_honam.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {
}

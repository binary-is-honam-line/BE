package com.oasis.binary_honam.repository;

import com.oasis.binary_honam.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}

package com.anton.webquizengine.repository;

import com.anton.webquizengine.model.QuizCompletion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizCompletionRepository extends JpaRepository<QuizCompletion, Integer> {
    Page<QuizCompletion> findByUser_Email(String email, Pageable pageable);
}


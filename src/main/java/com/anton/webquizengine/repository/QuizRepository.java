package com.anton.webquizengine.repository;

import com.anton.webquizengine.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
}

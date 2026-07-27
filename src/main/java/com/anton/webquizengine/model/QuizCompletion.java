package com.anton.webquizengine.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_completions")
public class QuizCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    protected QuizCompletion() {}

    public QuizCompletion(Integer quizId, User user, LocalDateTime completedAt) {
        this.quizId = quizId;
        this.user = user;
        this.completedAt = completedAt;
    }

    public Integer getId() {
        return id;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}

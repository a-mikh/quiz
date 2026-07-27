package com.anton.webquizengine.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import com.anton.webquizengine.dto.quiz.CreateQuizDto;
import com.anton.webquizengine.dto.quiz.QuizResponseDto;
import com.anton.webquizengine.dto.quiz.SolveRequestDto;
import com.anton.webquizengine.dto.quiz.SolveResponseDto;
import com.anton.webquizengine.dto.quiz_completion.QuizCompletionResponseDto;
import com.anton.webquizengine.service.QuizCompletionService;
import com.anton.webquizengine.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizService quizService;
    private final QuizCompletionService quizCompletionService;

    public QuizController(QuizService quizService,  QuizCompletionService quizCompletionService) {
        this.quizService = quizService;
        this.quizCompletionService = quizCompletionService;
    }

    @PostMapping
    public QuizResponseDto createQuiz(
            @Valid @RequestBody CreateQuizDto createQuizDto,
            Authentication authentication) {
        return quizService.create(createQuizDto, authentication.getName());
    }

    @GetMapping("/{id}")
    public QuizResponseDto getQuizById(@PathVariable int id) {
        return quizService.getById(id);
    }

    @GetMapping
    public Page<QuizResponseDto> getAll(@RequestParam(defaultValue = "0") @Min(0) int page) {
        return quizService.getAll(page);
    }

    @PostMapping("/{id}/solve")
    public SolveResponseDto solveQuiz(
            @PathVariable("id") int quizId,
            @RequestBody SolveRequestDto solveRequest,
            Authentication authentication
    ) {
        return quizService.solve(quizId, authentication.getName(), solveRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable int id,
            Authentication authentication
    ) {
        quizService.delete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/completed")
    public Page<QuizCompletionResponseDto> getCompletedQuizzes(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            Authentication authentication
    ) {
        return quizCompletionService.getCompletionsByUser(authentication.getName(), page);
    }

}

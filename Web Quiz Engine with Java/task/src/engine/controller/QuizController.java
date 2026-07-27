package engine.controller;

import engine.dto.quiz.CreateQuizDto;
import engine.dto.quiz.QuizResponseDto;
import engine.dto.quiz.SolveRequestDto;
import engine.dto.quiz.SolveResponseDto;
import engine.dto.quiz_completion.QuizCompletionResponseDto;
import engine.service.QuizCompletionService;
import engine.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

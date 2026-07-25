package engine.controller;

import engine.dto.quiz.CreateQuizDto;
import engine.dto.quiz.QuizResponseDto;
import engine.dto.quiz.SolveRequestDto;
import engine.dto.quiz.SolveResponseDto;
import engine.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public QuizResponseDto createQuiz(@Valid @RequestBody CreateQuizDto createQuizDto) {
        return quizService.create(createQuizDto);
    }

    @GetMapping("/{id}")
    public QuizResponseDto getQuizById(@PathVariable int id) {
        return quizService.getById(id);
    }

    @GetMapping
    public List<QuizResponseDto> getAll() {
        return quizService.getAll();
    }

    @PostMapping("/{id}/solve")
    public SolveResponseDto solveQuiz(@PathVariable("id") int quizId, @RequestBody SolveRequestDto solveRequest) {
        return quizService.solve(quizId, solveRequest);
    }

}

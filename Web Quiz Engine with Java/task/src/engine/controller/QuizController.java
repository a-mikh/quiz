package engine.controller;

import engine.dto.CreateQuizDto;
import engine.dto.QuizResponseDto;
import engine.dto.SolveResponseDto;
import engine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping()
    public QuizResponseDto createQuiz(@RequestBody CreateQuizDto createQuizDto) {
        return quizService.create(createQuizDto);
    }

    @GetMapping("/{id}")
    public QuizResponseDto getQuizById(@PathVariable int id) {
        return quizService.getById(id);
    }

    @GetMapping()
    public List<QuizResponseDto> getAll() {
        return null;
    }

    @PostMapping("/{id}/solve")
    public SolveResponseDto solveQuiz(@PathVariable("id") int quizId, @RequestParam int answer) {
        return null;
    }

}

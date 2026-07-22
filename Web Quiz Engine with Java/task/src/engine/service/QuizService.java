package engine.service;

import engine.dto.CreateQuizDto;
import engine.dto.QuizResponseDto;
import engine.dto.SolveResponseDto;
import engine.exception.QuizNotFoundException;
import engine.model.Quiz;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {
    private static final String CORRECT_FEEDBACK =
            "Congratulations, you're right!";

    private static final String WRONG_FEEDBACK =
            "Wrong answer! Please, try again.";

    private Map<Integer, Quiz> quizzes = new HashMap<>();
    private int nextId = 1;

    public QuizResponseDto create(CreateQuizDto createQuizDto) {
        List<String> options = createQuizDto.getOptions();

        if (options == null) {
            options = new ArrayList<>();
        } else {
            options = new ArrayList<>(options);
        }

        Quiz quiz = new Quiz(
                nextId,
                createQuizDto.getTitle(),
                createQuizDto.getText(),
                options,
                createQuizDto.getAnswer());
        quizzes.put(quiz.getId(), quiz);
        nextId++;

        return toResponseDto(quiz);
    }

    public QuizResponseDto getById(int id) {
        Quiz quiz = quizzes.get(id);

        if (quiz == null) {
            throw new QuizNotFoundException();
        }

        return toResponseDto(quiz);
    }

    public List<QuizResponseDto> getAll() {
        return quizzes.values()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public SolveResponseDto solve(int quizId, int answer) {
        Quiz quiz = quizzes.get(quizId);

        if (quiz == null) {
            throw new QuizNotFoundException();
        }

        if (quiz.getAnswer() == null || !quiz.getAnswer().equals(answer)) {
            return new SolveResponseDto(false, WRONG_FEEDBACK);
        }

        return new  SolveResponseDto(true, CORRECT_FEEDBACK);
    }

    private QuizResponseDto toResponseDto(Quiz quiz) {
        return new  QuizResponseDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getText(),
                quiz.getOptions()
        );
    }
}

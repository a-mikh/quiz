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
        return null;
    }

    public SolveResponseDto solve(int quizId, int answer) {
        return null;
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

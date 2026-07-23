package engine.service;

import engine.dto.CreateQuizDto;
import engine.dto.QuizResponseDto;
import engine.dto.SolveRequestDto;
import engine.dto.SolveResponseDto;
import engine.exception.InvalidQuizException;
import engine.exception.QuizNotFoundException;
import engine.model.Quiz;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {
    private static final String CORRECT_FEEDBACK =
            "Congratulations, you're right!";

    private static final String WRONG_FEEDBACK =
            "Wrong answer! Please, try again.";

    private Map<Integer, Quiz> quizzes = new HashMap<>();
    private int nextId = 1;

    public QuizResponseDto create(CreateQuizDto createQuizDto) {
        List<Integer> answers;

        if (createQuizDto.getAnswer() == null) {
            answers = new ArrayList<>();
        } else {
            answers = new ArrayList<>(createQuizDto.getAnswer());
        }

        for (int answer : answers) {
            if (answer > createQuizDto.getOptions().size() - 1 || answer < 0) {
                throw new InvalidQuizException();
            }
        }

        Quiz quiz = new Quiz(
                nextId,
                createQuizDto.getTitle(),
                createQuizDto.getText(),
                createQuizDto.getOptions(),
                answers);
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

    public SolveResponseDto solve(int quizId, SolveRequestDto solveRequest) {
        Quiz quiz = quizzes.get(quizId);

        if (quiz == null) {
            throw new QuizNotFoundException();
        }

        List<Integer> userAnswer = solveRequest.getAnswer() == null
                ? new ArrayList<>()
                : new ArrayList<>(solveRequest.getAnswer());

        if (quiz.getAnswer().size() != userAnswer.size()) {
            return new SolveResponseDto(false, WRONG_FEEDBACK);
        }

        Set<Integer> userAnswerSet = new HashSet<>(userAnswer);

        if (!userAnswerSet.containsAll(quiz.getAnswer())) {
            return new SolveResponseDto(false, WRONG_FEEDBACK);
        }

        return new SolveResponseDto(true, CORRECT_FEEDBACK);
    }

    private QuizResponseDto toResponseDto(Quiz quiz) {
        return new QuizResponseDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getText(),
                quiz.getOptions()
        );
    }
}

package engine.service;

import engine.dto.quiz.CreateQuizDto;
import engine.dto.quiz.QuizResponseDto;
import engine.dto.quiz.SolveRequestDto;
import engine.dto.quiz.SolveResponseDto;
import engine.exception.quiz.InvalidQuizException;
import engine.exception.quiz.QuizAccessDeniedException;
import engine.exception.quiz.QuizNotFoundException;
import engine.exception.user.UserNotFoundException;
import engine.model.Quiz;
import engine.model.User;
import engine.repository.QuizRepository;
import engine.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class QuizService {
    private static final String CORRECT_FEEDBACK =
            "Congratulations, you're right!";

    private static final String WRONG_FEEDBACK =
            "Wrong answer! Please, try again.";

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public QuizResponseDto create(CreateQuizDto createQuizDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

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
                createQuizDto.getTitle(),
                createQuizDto.getText(),
                createQuizDto.getOptions(),
                answers,
                user);
        Quiz savedQuiz = quizRepository.save(quiz);

        return toResponseDto(savedQuiz);
    }

    public QuizResponseDto getById(int id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(QuizNotFoundException::new);

        return toResponseDto(quiz);
    }

    public List<QuizResponseDto> getAll() {
        return StreamSupport.stream(quizRepository.findAll().spliterator(), false)
                .map(this::toResponseDto)
                .toList();
    }

    public SolveResponseDto solve(int quizId, SolveRequestDto solveRequest) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(QuizNotFoundException::new);


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

    @Transactional
    public void delete(int id, String userEmail) {
        Quiz quiz =  quizRepository.findById(id)
                .orElseThrow(QuizNotFoundException::new);

        if (!quiz.getAuthor().getEmail().equals(userEmail)) {
            throw new QuizAccessDeniedException();
        }

        quizRepository.delete(quiz);
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

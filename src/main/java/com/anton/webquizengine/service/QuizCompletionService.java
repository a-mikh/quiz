package com.anton.webquizengine.service;

import com.anton.webquizengine.dto.quiz_completion.QuizCompletionResponseDto;
import com.anton.webquizengine.exception.user.UserNotFoundException;
import com.anton.webquizengine.model.QuizCompletion;
import com.anton.webquizengine.model.User;
import com.anton.webquizengine.repository.QuizCompletionRepository;
import com.anton.webquizengine.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class QuizCompletionService {
    private static final int PAGE_SIZE = 10;

    private final QuizCompletionRepository quizCompletionRepository;
    private final UserRepository userRepository;

    public QuizCompletionService(QuizCompletionRepository quizCompletionRepository, UserRepository userRepository) {
        this.quizCompletionRepository = quizCompletionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<QuizCompletionResponseDto> getCompletionsByUser(String email, int page) {
        Sort sort = Sort.by(
                Sort.Order.desc("completedAt"),
                Sort.Order.desc("id")
        );
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
        Page<QuizCompletion> quizCompletionPage = quizCompletionRepository.findByUser_Email(email, pageable);

        return quizCompletionPage.map(completion -> new QuizCompletionResponseDto(
                completion.getQuizId(),
                completion.getCompletedAt()
        ));
    }

    @Transactional
    public void recordSuccessfulCompletion(Integer quizId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        QuizCompletion quizCompletion = new QuizCompletion(
                quizId,
                user,
                LocalDateTime.now()
        );

        quizCompletionRepository.save(quizCompletion);
    }
}

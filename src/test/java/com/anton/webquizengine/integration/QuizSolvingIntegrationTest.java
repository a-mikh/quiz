package com.anton.webquizengine.integration;

import com.anton.webquizengine.model.Quiz;
import com.anton.webquizengine.model.QuizCompletion;
import com.anton.webquizengine.model.User;
import com.anton.webquizengine.repository.QuizCompletionRepository;
import com.anton.webquizengine.repository.QuizRepository;
import com.anton.webquizengine.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuizSolvingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizCompletionRepository quizCompletionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        quizCompletionRepository.deleteAll();
        quizRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnSuccessAndRecordCompletionForCorrectAnswer() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        var savedUser = saveUser(email, password);
        var savedQuiz = quizRepository.save(new Quiz(
                "test title",
                "test text",
                List.of("1", "2", "3"),
                List.of(0),
                savedUser
        ));

        String request = """
                {
                  "answer": [0]
                }
                """;

        mockMvc.perform(post("/api/quizzes/" + savedQuiz.getId() + "/solve")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.feedback")
                        .value("Congratulations, you're right!"));

        assertThat(quizRepository.count()).isOne();
        assertThat(quizCompletionRepository.count()).isOne();
        var completion = quizCompletionRepository.findAll().getFirst();

        assertThat(completion.getQuizId())
                .isEqualTo(savedQuiz.getId());

        assertThat(completion.getUser().getId())
                .isEqualTo(savedUser.getId());

        assertThat(completion.getCompletedAt())
                .isNotNull();
    }

    @Test
    void shouldReturnFailureAndNotRecordCompletionForIncorrectAnswer() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        var savedUser = saveUser(email, password);
        var savedQuiz = quizRepository.save(new Quiz(
                "test title",
                "test text",
                List.of("1", "2", "3"),
                List.of(0),
                savedUser
        ));

        String request = """
                {
                  "answer": [1]
                }
                """;

        mockMvc.perform(post("/api/quizzes/" + savedQuiz.getId() + "/solve")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.feedback").value("Wrong answer! Please, try again."));

        assertThat(quizRepository.count()).isOne();
        assertThat(quizCompletionRepository.count()).isZero();
    }

    @Test
    void shouldReturn401WhenSolvingQuizWithoutAuthentication() throws Exception {
        User savedUser = saveUser("test@email.com", "password");
        Quiz savedQuiz = quizRepository.save(new Quiz(
                "test title",
                "test text",
                List.of("1", "2", "3"),
                List.of(0),
                savedUser
        ));

        String request = """
                {
                  "answer": [0]
                }
                """;

        mockMvc.perform(post("/api/quizzes/" + savedQuiz.getId() + "/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());

        assertThat(quizRepository.count()).isOne();
        assertThat(quizCompletionRepository.count()).isZero();
    }

    @Test
    void shouldReturn404WhenQuizDoesNotExist() throws Exception {
        String email = "user@example.com";
        String password = "secret123";
        String request = """
                        {
                          "answer": [0]
                        }
                """;

        saveUser(email, password);

        mockMvc.perform(post("/api/quizzes/999/solve")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());

        assertThat(quizRepository.count()).isZero();
        assertThat(quizCompletionRepository.count()).isZero();
    }

    @Test
    void shouldReturnSuccessWhenQuizHasNoCorrectAnswersAndSubmittedAnswerIsEmpty() throws Exception {
        String email = "user@example.com";
        String password = "secret123";
        User savedUser = saveUser(email, password);
        Quiz savedQuiz = quizRepository.save(new Quiz(
                "test title",
                "test text",
                List.of("1", "2", "3"),
                List.of(),
                savedUser
        ));

        String request = """
                        {
                          "answer": []
                        }
                """;

        mockMvc.perform(post("/api/quizzes/" + savedQuiz.getId() + "/solve")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.feedback").value("Congratulations, you're right!"));

        assertThat(quizCompletionRepository.count()).isOne();
    }

    @Test
    void shouldCreateDifferentRecordsForTwoSuccessfulCompletions() throws Exception {
        String email = "user@example.com";
        String password = "secret123";
        User savedUser = saveUser(email, password);
        Quiz savedQuiz = quizRepository.save(new Quiz(
                "test title",
                "test text",
                List.of("1", "2", "3"),
                List.of(),
                savedUser
        ));

        String request = """
                        {
                          "answer": []
                        }
                """;

        mockMvc.perform(post("/api/quizzes/" + savedQuiz.getId() + "/solve")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.feedback").value("Congratulations, you're right!"));

        mockMvc.perform(post("/api/quizzes/" + savedQuiz.getId() + "/solve")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.feedback").value("Congratulations, you're right!"));

        var completions = quizCompletionRepository.findAll();

        assertThat(completions)
                .hasSize(2)
                .extracting(QuizCompletion::getId)
                .doesNotHaveDuplicates();
        assertThat(completions)
                .allMatch(completion ->
                        completion.getQuizId().equals(savedQuiz.getId()));
    }

    @Test
    void shouldReturnSuccessForAnswersInRandomOrder() throws Exception {
        String email = "user@example.com";
        String password = "secret123";
        User savedUser = saveUser(email, password);
        Quiz savedQuiz = quizRepository.save(new Quiz(
                "test title",
                "test text",
                List.of("1", "2", "3"),
                List.of(0, 2),
                savedUser
        ));

        String request = """
                        {
                          "answer": [2, 0]
                        }
                """;

        mockMvc.perform(post("/api/quizzes/" + savedQuiz.getId() + "/solve")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.feedback").value("Congratulations, you're right!"));

        assertThat(quizCompletionRepository.count()).isOne();
    }

    private User saveUser(String email, String rawPassword) {
        User user = new User(
                email,
                passwordEncoder.encode(rawPassword)
        );

        return userRepository.save(user);
    }
}

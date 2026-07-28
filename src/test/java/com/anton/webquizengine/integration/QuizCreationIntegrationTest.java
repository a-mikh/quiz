package com.anton.webquizengine.integration;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuizCreationIntegrationTest {
    private static final String VALID_QUIZ_REQUEST = """
            {
              "title": "test title",
              "text": "test text",
              "options": ["1", "2", "3"],
              "answer": [0]
            }
            """;

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
    void shouldReturn401WhenCreatingQuizWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_QUIZ_REQUEST))
                .andExpect(status().isUnauthorized());

        assertThat(quizRepository.count()).isZero();
    }

    @Test
    void shouldCreateQuizForAuthorizedUser() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        var savedUser = saveUser(email, password);

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_QUIZ_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("test title"))
                .andExpect(jsonPath("$.text").value("test text"))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options.length()").value(3))
                .andExpect(jsonPath("$.answer").doesNotExist());

        assertThat(quizRepository.count()).isOne();
        var savedQuiz = quizRepository.findAll().getFirst();

        assertThat(savedQuiz.getAuthor().getId())
                .isEqualTo(savedUser.getId());
        assertThat(savedQuiz.getTitle()).isEqualTo("test title");
        assertThat(savedQuiz.getText()).isEqualTo("test text");
        assertThat(savedQuiz.getOptions())
                .containsExactly("1", "2", "3");
        assertThat(savedQuiz.getAnswer())
                .containsExactly(0);
    }

    @Test
    void shouldReturn401WhenPasswordIsIncorrect() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, "wrong-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_QUIZ_REQUEST))
                .andExpect(status().isUnauthorized());

        assertThat(quizRepository.count()).isZero();
    }

    @Test
    void shouldReturn400ForBlankTitle() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        String invalidQuizRequest = """
                  {
                    "title": "    ",
                    "text": "test text",
                    "options": ["1", "2", "3"],
                    "answer": [0]
                  }
                """;

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidQuizRequest))
                .andExpect(status().isBadRequest());

        assertThat(quizRepository.count()).isZero();
    }

    @Test
    void shouldReturn400ForBlankText() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        String invalidQuizRequest = """
                  {
                    "title": "test title",
                    "text": "   ",
                    "options": ["1", "2", "3"],
                    "answer": [0]
                  }
                """;

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidQuizRequest))
                .andExpect(status().isBadRequest());

        assertThat(quizRepository.count()).isZero();
    }

    @Test
    void shouldReturn400ForLessThanTwoOptions() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        String invalidQuizRequest = """
                  {
                    "title": "test title",
                    "text": "test text",
                    "options": ["1"],
                    "answer": [0]
                  }
                """;

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidQuizRequest))
                .andExpect(status().isBadRequest());

        assertThat(quizRepository.count()).isZero();
    }

    @Test
    void shouldReturn400WhenOptionsAreMissing() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        String invalidQuizRequest = """
                  {
                    "title": "test title",
                    "text": "test text",
                    "answer": [0]
                  }
                """;

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidQuizRequest))
                .andExpect(status().isBadRequest());

        assertThat(quizRepository.count()).isZero();
    }

    @Test
    void shouldReturn400WhenOptionsAreNull() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        String invalidQuizRequest = """
                  {
                    "title": "test title",
                    "text": "test text",
                    "options": null,
                    "answer": [0]
                  }
                """;

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidQuizRequest))
                .andExpect(status().isBadRequest());

        assertThat(quizRepository.count()).isZero();
    }

    @Test
    void shouldCreateQuizWithTwoOptions() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        String quizRequest = """
                  {
                    "title": "test title",
                    "text": "test text",
                    "options": ["1", "2"],
                    "answer": [0]
                  }
                """;

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(quizRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").doesNotExist());

        assertThat(quizRepository.count()).isOne();
        assertThat(quizRepository.findAll().get(0).getOptions()).containsExactly("1", "2");
    }

    @Test
    void shouldCreateQuizWithNoAnswer() throws Exception {
        String email = "user@example.com";
        String password = "secret123";

        saveUser(email, password);

        String quizRequest = """
                  {
                    "title": "test title",
                    "text": "test text",
                    "options": ["1", "2"]
                  }
                """;

        mockMvc.perform(post("/api/quizzes")
                        .with(httpBasic(email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(quizRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").doesNotExist());

        assertThat(quizRepository.count()).isOne();
        var savedQuiz = quizRepository.findAll().getFirst();

        assertThat(savedQuiz.getAnswer()).isEmpty();
    }

    private User saveUser(String email, String rawPassword) {
        User user = new User(
                email,
                passwordEncoder.encode(rawPassword)
        );

        return userRepository.save(user);
    }
}

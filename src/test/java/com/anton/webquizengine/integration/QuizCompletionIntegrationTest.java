package com.anton.webquizengine.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuizCompletionIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn401OnCompletedForUnauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/quizzes/completed"))
                .andExpect(status().isUnauthorized());
    }
}

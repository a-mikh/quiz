package com.anton.webquizengine.dto.quiz_completion;

import java.time.LocalDateTime;

public record QuizCompletionResponseDto(
        Integer id,
        LocalDateTime completedAt
) {
}

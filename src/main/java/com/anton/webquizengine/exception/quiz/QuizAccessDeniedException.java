package com.anton.webquizengine.exception.quiz;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class QuizAccessDeniedException extends  RuntimeException
{
}

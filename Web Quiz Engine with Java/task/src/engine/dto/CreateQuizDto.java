package engine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateQuizDto {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Text is required")
    private String text;
    @NotNull(message = "Options cannot be null")
    @Size(min = 2, message = "Options must contain at least 2 items")
    private List<String> options;
    private List<Integer> answer;

    public CreateQuizDto() {}

    public CreateQuizDto(String title, String text, List<String> options, List<Integer> answer) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }
}

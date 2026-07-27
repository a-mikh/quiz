package com.anton.webquizengine.dto.quiz;

import java.util.List;

public class QuizResponseDto {
    private int id;
    private String title;
    private String text;
    private List<String> options;

    public QuizResponseDto() {}

    public QuizResponseDto(int id, String title, String text, List<String> options) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}

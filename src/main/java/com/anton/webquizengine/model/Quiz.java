package com.anton.webquizengine.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String text;
    @Fetch(FetchMode.SUBSELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "option_position")
    private List<String> options;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> answer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    protected Quiz() {
    }

    public Quiz(String title, String text, List<String> options, List<Integer> answer, User author) {
        this.title = title;
        this.text = text;
        this.options = new ArrayList<>(options);
        this.answer = new ArrayList<>(answer);
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public User getAuthor() {
        return author;
    }
}

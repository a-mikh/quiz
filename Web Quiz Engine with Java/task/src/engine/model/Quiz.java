package engine.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String text;
    @ElementCollection
    @OrderColumn(name = "option_position")
    private List<String> options;
    @ElementCollection
    private List<Integer> answer;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    protected Quiz() {
    }

    public Quiz(String title, String text, List<String> options, List<Integer> answer, User author) {
        this.title = title;
        this.text = text;
        this.options = new ArrayList<>(options);
        this.answer = answer;
        this.author = author;
    }

    public Integer getId() {
        return id;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}

package engine.dto.quiz;

import java.util.List;

public class SolveRequestDto {
    private List<Integer> answer;

    public SolveRequestDto() {}

    public SolveRequestDto(List<Integer> answer) {
        this.answer = answer;
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }
}

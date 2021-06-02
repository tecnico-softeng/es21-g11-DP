package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.Column;

public class OpenAnswerQuestionDto extends QuestionDetailsDto {
    private String correctAnswer;

    public OpenAnswerQuestionDto () {
    }

    public OpenAnswerQuestionDto(OpenAnswerQuestion question) {
        this.correctAnswer = question.getCorrectAnswer();
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new OpenAnswerQuestion(question, this);
    }

    @Override
    public void update(OpenAnswerQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "OpenAnswerQuestionDto{" +
                "correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}

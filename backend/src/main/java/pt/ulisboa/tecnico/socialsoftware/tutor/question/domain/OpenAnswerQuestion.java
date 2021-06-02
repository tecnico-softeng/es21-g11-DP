package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ANSWER_QUESTION)
public class OpenAnswerQuestion extends QuestionDetails {

    @Column(columnDefinition = "TEXT")
    private String correctAnswer; //resposta prof

    public OpenAnswerQuestion() {
        super();
    }

    public OpenAnswerQuestion(Question question, OpenAnswerQuestionDto questionDto) {
        super(question);
        setCorrectAnswer(questionDto.getCorrectAnswer());
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String toString() {
        return "OpenAnswerQuestion{" +
                "correctAnswer='" + correctAnswer + '\'' +
                '}';
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new OpenAnswerCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new OpenAnswerStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new OpenAnswerStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new OpenAnswerAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new OpenAnswerQuestionDto(this);
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    public void update(OpenAnswerQuestionDto questionDetails) {
        setCorrectAnswer(questionDetails.getCorrectAnswer());
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return this.correctAnswer;
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null;
    }
}

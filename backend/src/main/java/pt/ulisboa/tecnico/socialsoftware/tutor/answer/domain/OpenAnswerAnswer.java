package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenAnswerAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenAnswerStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ANSWER_QUESTION)
public class OpenAnswerAnswer extends AnswerDetails {

    @Column(columnDefinition = "TEXT")
    private String studentAnswer;
    @Column(columnDefinition = "TEXT")
    private String teacherCorrectAnswer;


    public OpenAnswerAnswer() {super();}

    public OpenAnswerAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);
    }

    public OpenAnswerAnswer(QuestionAnswer questionAnswer, String receivedAnswer) {
        super(questionAnswer);
        this.setStudentAnswer(receivedAnswer);
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String receivedAnswer) {
        this.studentAnswer = receivedAnswer;
    }

    public String getTeacherCorrectAnswer() {
        return teacherCorrectAnswer;
    }

    public void setStudentAnswer(OpenAnswerQuestion question, OpenAnswerStatementAnswerDetailsDto openAnswerStatementAnswerDetailsDto){
        teacherCorrectAnswer = question.getCorrectAnswer();

        if(openAnswerStatementAnswerDetailsDto.getStudentAnswer() != null) {
            if(this.getStudentAnswer() != null){
                this.setStudentAnswer(null);
            }
            this.setStudentAnswer(openAnswerStatementAnswerDetailsDto.getStudentAnswer());
        } else {
            this.setStudentAnswer(null);
        }
    }

    @Override
    public boolean isCorrect() {
        return (getStudentAnswer() != null && (getStudentAnswer().compareTo(getTeacherCorrectAnswer()) == 0) );
    }

    @Override
    public void remove() {
        if(studentAnswer != null){
            this.setStudentAnswer(null);
        }
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new OpenAnswerAnswerDto(this);
    }

    @Override
    public String getAnswerRepresentation() {
        return this.studentAnswer != null ? this.studentAnswer : "not_Answered";
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new OpenAnswerStatementAnswerDetailsDto(this);
    }

    @Override
    public boolean isAnswered() {
        return this.studentAnswer != null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}

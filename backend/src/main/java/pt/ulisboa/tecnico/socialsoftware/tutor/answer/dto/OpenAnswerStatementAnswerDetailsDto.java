package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion;

public class OpenAnswerStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private String studentAnswer;

    public OpenAnswerStatementAnswerDetailsDto() {
    }

    public OpenAnswerStatementAnswerDetailsDto (OpenAnswerAnswer questionAnswer) {
        if(questionAnswer.getStudentAnswer() != null) {
            this.studentAnswer = questionAnswer.getStudentAnswer();
        }
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    private OpenAnswerAnswer createdOpenAnswerAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdOpenAnswerAnswer = new OpenAnswerAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        //questionAnswer >> question do quiz >> questiondetails >> ...
        return createdOpenAnswerAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return studentAnswer == null;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto){
        return new OpenAnswerAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public void update(OpenAnswerQuestion question) {
        createdOpenAnswerAnswer.setStudentAnswer(question, this);
    }

    @Override
    public String toString() {
        return "OpenAnswerStatementAnswerDetailsDto{" +
                "studentAnswer='" + studentAnswer + '\'' +
                '}';
    }
}

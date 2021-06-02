package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenAnswerAnswer;

public class OpenAnswerAnswerDto extends AnswerDetailsDto {
    private String studentAnswer;

    public OpenAnswerAnswerDto() {
    }

    public OpenAnswerAnswerDto(OpenAnswerAnswer openAnswerAnswer) {
        if(openAnswerAnswer.getStudentAnswer() != null) {
            this.studentAnswer = openAnswerAnswer.getStudentAnswer();
        }
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }
}

package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.PCIQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PCIQuestionDto extends QuestionDetailsDto {

    private List<PCIItemDto> itemGroupA = new ArrayList<>();
    private List<PCIItemDto> itemGroupB = new ArrayList<>();

    public PCIQuestionDto() {
    }

    public PCIQuestionDto(PCIQuestion question) {
        this.itemGroupA = question.getItemGroupA().stream().map(PCIItemDto::new).collect(Collectors.toList());
        this.itemGroupB = question.getItemGroupB().stream().map(PCIItemDto::new).collect(Collectors.toList());
    }

    public List<PCIItemDto> getItemGroupA() {
        return itemGroupA;
    }
    public List<PCIItemDto> getItemGroupB() {
        return itemGroupB;
    }

    public void setItemGroupA(List<PCIItemDto> itemGroupA) {
        this.itemGroupA = itemGroupA;
    }

    public void setItemGroupB(List<PCIItemDto> itemGroupB) {
        this.itemGroupB = itemGroupB;
    }

    @Override
    public String toString() {
        return "PCIQuestionDto{" +
                "itemGroupA=" + itemGroupA +
                "itemGroupB=" + itemGroupB +
                '}';
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new PCIQuestion(question, this);
    }

    @Override
    public void update(PCIQuestion question) {
        question.update(this);
    }
}

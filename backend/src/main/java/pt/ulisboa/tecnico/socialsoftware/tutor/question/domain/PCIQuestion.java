package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class PCIQuestion extends QuestionDetails {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PCIItem> itemGroupA = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PCIItem> itemGroupB = new ArrayList<>();


    public PCIQuestion() {
        super();
    }

    public PCIQuestion(Question question, PCIQuestionDto pciQuestionDto) {
        super(question);
        update(pciQuestionDto);
    }

    public List<PCIItem> getItemGroupA() {
        return itemGroupA;
    }

    public void setItemGroupA(List<PCIItemDto> pciItemDtos) {
        for (PCIItemDto pciItemDto : pciItemDtos) {
            if (pciItemDto.getId() == null) {
                PCIItem pciItem = new PCIItem(pciItemDto, itemGroupB);
                pciItem.setQuestionDetails(this);
                this.itemGroupA.add(pciItem);
            } else {
                PCIItem pciItem = getItemGroupA()
                        .stream()
                        .filter(op -> op.getId().equals(pciItemDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(ITEM_NOT_FOUND, pciItemDto.getId()));

                pciItem.setContent(pciItemDto.getContent());
                pciItem.setCorresponding(pciItemDto.getCorresponding(), itemGroupB);
            }
        }
    }

    public List<PCIItem> getItemGroupB() {
        return itemGroupB;
    }

    public void setItemGroupB(List<PCIItemDto> pciItemDtos) {
        for (PCIItemDto pciItemDto : pciItemDtos) {
            if (pciItemDto.getId() == null) {
                PCIItem pciItem = new PCIItem(pciItemDto);
                pciItem.setQuestionDetails(this);
                this.itemGroupB.add(pciItem);
            } else {
                PCIItem pciItem = getItemGroupB()
                        .stream()
                        .filter(op -> op.getId().equals(pciItemDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(ITEM_NOT_FOUND, pciItemDto.getId()));

                pciItem.setContent(pciItemDto.getContent());
                pciItem.setCorresponding(pciItemDto.getCorresponding());
            }
        }
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return null;
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return null;
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new PCIQuestionDto(this);
    }

    @Override
    public void delete() {
        super.delete();
        for (var items : this.itemGroupA) {
            items.delete();
        }
        this.itemGroupA.clear();

        for (var items : this.itemGroupB) {
            items.delete();
        }
        this.itemGroupB.clear();
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return null;
    }

    public void update(PCIQuestionDto questionDetails) {
        setItemGroupB(questionDetails.getItemGroupB());
        setItemGroupA(questionDetails.getItemGroupA());
//        setItemGroupA(questionDetails.getItemGroupA());
//        setItemGroupB(questionDetails.getItemGroupB());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitPCIItems(Visitor visitor) {
        for (var item : this.getItemGroupA()) {
            item.accept(visitor);
        }

        for (var item : this.getItemGroupB()) {
            item.accept(visitor);
        }
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null;
    }
}

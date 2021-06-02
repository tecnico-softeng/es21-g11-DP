package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceQuestion extends QuestionDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<Option> options = new ArrayList<>();
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean orderableByRelevance;


    public MultipleChoiceQuestion() {
        super();
        setOrderableByRelevance(false);
    }

    public MultipleChoiceQuestion(Question question, MultipleChoiceQuestionDto questionDto) {
        super(question);
        setOptions(questionDto.getOptions());
    }

    public boolean isOrderableByRelevance() {
        return orderableByRelevance;
    }

    public void setOrderableByRelevance(boolean isOrderableByRelevance) {
        this.orderableByRelevance = isOrderableByRelevance;
    }


    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {

        checkCorrectOptionsExistence(options);

        List<Integer> relevancesList = getRelevancesList(options);

        Set<Integer> relevancesSet = new HashSet<Integer>(relevancesList);

        allDiferentRelevances(relevancesList, relevancesSet);

        updateOrderableByRelevance(relevancesList);

        List<Integer> optionIds = new ArrayList<>();
        for (OptionDto option : options){
            optionIds.add(option.getId());
        }
        List<Option> optionsToRemove = new ArrayList<>();
        for (Option option: getOptions()){
            if (!optionIds.contains(option.getId())){
                optionsToRemove.add(option);
                option.remove();
            }
        }
        getOptions().removeAll(optionsToRemove);

        int index = 0;
        for (OptionDto optionDto : options) {
            if (optionDto.getId() == null) {
                optionDto.setSequence(index++);
                new Option(optionDto).setQuestionDetails(this);
            } else {
                addEachOption(optionDto);
            }
        }

    }


    private void allDiferentRelevances(List<Integer> relevancesList, Set<Integer> relevancesSet) {
        if (relevancesSet.size() < relevancesList.size()) {
            throw new TutorException(TWO_OPTIONS_SAME_RELEVANCE);
        }
    }

    private void updateOrderableByRelevance(List<Integer> relevancesList) {
        setOrderableByRelevance(relevancesList.size() > 1);
    }

    private List<Integer> getRelevancesList(List<OptionDto> options) {
        List<Integer> relevancesList = new ArrayList<Integer>();
        for (OptionDto optionDto : options) {
            if (optionDto.getRelevance() != -1) {
                relevancesList.add(optionDto.getRelevance());
            }
        }
        return relevancesList;
    }

    private void checkCorrectOptionsExistence(List<OptionDto> options) {
        if (options.stream().noneMatch(OptionDto::isCorrect)) {
            throw new TutorException(AT_LEAST_ONE_CORRECT_OPTION_NEEDED);
        }
    }

    private void addEachOption(OptionDto optionDto) {
        Option option = getOptions()
                .stream()
                .filter(op -> op.getId().equals(optionDto.getId()))
                .findAny()
                .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, optionDto.getId()));

        option.setContent(optionDto.getContent());
        option.setRelevance(optionDto.getRelevance());
        option.setCorrect(optionDto.isCorrect());

    }

    public void addOption(Option option) {
        options.add(option);
    }

    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::isCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }

    public List<Integer> getCorrectOptionIds() {
        return this.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(Collectors.toList());
    }

    public List<Integer> orderByRelevanceIds() {
        return this.getOptions().stream()
                .filter(op -> op.getRelevance() != -1)
                .sorted((op1, op2) -> Integer.compare(op2.getRelevance(), op1.getRelevance()))
                .map(Option::getId)
                .collect(Collectors.toList());
    }

    public void update(MultipleChoiceQuestionDto questionDetails) {
        setOptions(questionDetails.getOptions());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return convertSequenceToLetter(this.getCorrectAnswer());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitOptions(Visitor visitor) {
        for (Option option : this.getOptions()) {
            option.accept(visitor);
        }
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new MultipleChoiceCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new MultipleChoiceStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new MultipleChoiceStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new MultipleChoiceAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new MultipleChoiceQuestionDto(this);
    }

    public Integer getCorrectAnswer() {
        return this.getOptions()
                .stream()
                .filter(Option::isCorrect)
                .findAny().orElseThrow(() -> new TutorException(NO_CORRECT_OPTION))
                .getSequence();
    }

    @Override
    public void delete() {
        super.delete();
        for (Option option : this.options) {
            
            option.remove();
        }
        this.options.clear();
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestion{" +
                "options=" + options +
                '}';
    }

    public static String convertSequenceToLetter(Integer correctAnswer) {
        return correctAnswer != null ? Character.toString('A' + correctAnswer) : "-";
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        var result = this.options
                .stream()
                .filter(x -> selectedIds.contains(x.getId()))
                .map(x -> convertSequenceToLetter(x.getSequence()))
                .collect(Collectors.joining("|"));
        return !result.isEmpty() ? result : "-";
    }

}

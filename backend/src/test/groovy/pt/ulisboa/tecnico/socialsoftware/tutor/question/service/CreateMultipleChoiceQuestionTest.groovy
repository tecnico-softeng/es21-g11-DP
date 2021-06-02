package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class CreateMultipleChoiceQuestionTest extends SpockTest {


    def "create multiple choice question with 4 options and 2 correct"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        def questionDetails = new MultipleChoiceQuestion()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto(questionDetails))
        questionDetailsRepository.save(questionDetails)

        and: "4 options with 2 correct"
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        optionDto1.setSequence(0)

        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_2_CONTENT)
        optionDto2.setCorrect(true)
        optionDto2.setSequence(1)

        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_3_CONTENT)
        optionDto3.setCorrect(false)
        optionDto3.setSequence(2)

        def optionDto4 = new OptionDto()
        optionDto4.setContent(OPTION_4_CONTENT)
        optionDto4.setCorrect(false)
        optionDto4.setSequence(3)

        def options = new ArrayList<OptionDto>()

        options.add(optionDto2)
        options.add(optionDto3)
        options.add(optionDto4)
        options.add(optionDto1)

        questionDto.getQuestionDetailsDto().setOptions(options)


        when:

        questionService.createQuestion(externalCourse.getId(), questionDto)


        then: "the correct question is inside the repository"

        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        def op1 = result.getQuestionDetails().getOptions().get(3)
        def op2 = result.getQuestionDetails().getOptions().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetails().getOptions().size() == 4
        result.getQuestionDetails().getCorrectOptionIds().size() == 2
        result.getQuestionDetails().getCorrectOptionIds().contains(op1.getId())
        result.getQuestionDetails().getCorrectOptionIds().contains(op2.getId())
    }

    def "create multiple choice question with only 1 false option"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: '3 optionId with no correct'
        def optionDto1 = new OptionDto()
        def optionDto2 = new OptionDto()
        def optionDto3 = new OptionDto()
        def options = new ArrayList<OptionDto>()
        optionDto1.setContent(OPTION_1_CONTENT)
        options.add(optionDto1)
        optionDto2.setContent(OPTION_2_CONTENT)
        options.add(optionDto2)
        optionDto3.setContent(OPTION_3_CONTENT)
        options.add(optionDto3)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_CORRECT_OPTION_NEEDED

    }

    def "create multiple choice question with 3 options and 2 to order"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: '3 options with 2 orderable'
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        optionDto1.setRelevance(1)
        optionDto1.setSequence(0)

        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_2_CONTENT)
        optionDto2.setCorrect(true)
        optionDto2.setRelevance(2)
        optionDto2.setSequence(1)

        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_3_CONTENT)
        optionDto3.setCorrect(false)
        optionDto3.setRelevance(-1)
        optionDto3.setSequence(2)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto1)
        options.add(optionDto2)
        options.add(optionDto3)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)

        def op1 = result.getQuestionDetails().getOptions().get(0)
        def op2 = result.getQuestionDetails().getOptions().get(1)


        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetails().getOptions().size() == 3
        result.getQuestionDetails().getCorrectOptionIds().size() == 2
        result.getQuestionDetails().getCorrectOptionIds().contains(op1.getId())
        result.getQuestionDetails().getCorrectOptionIds().contains(op2.getId())
        result.getQuestionDetails().orderByRelevanceIds().size() == 2
        result.getQuestionDetails().orderByRelevanceIds().get(0).equals(op2.getId())
        result.getQuestionDetails().orderByRelevanceIds().get(1).equals(op1.getId())

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

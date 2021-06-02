package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@DataJpaTest
class UpdateMultipleChoiceQuestionTest extends SpockTest {
    def question
    def optionOK
    def optionKO
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: 'two options'
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setRelevance(-1)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setRelevance(-1)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

    }


    def "update number of correct from one to many"() {
        //create more correct options
        given: "a question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is not changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getDifficulty() == 50
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_1_CONTENT
        resOptionOne.isCorrect()
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_1_CONTENT
        resOptionTwo.isCorrect()
    }

    def "update number of correct to zero"() {
        //exception
        given: "a question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_CORRECT_OPTION_NEEDED

    }

    def "update relevance order with only one correct answer"() {
        //add more correct answers
        given: "an question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        optionDto.setRelevance(1)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "question is changed to orderable by relevance"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getQuestionDetails().isOrderableByRelevance()
        and: "are not changed"
        result.getId() == question.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() == null
        and: "an option is changed"
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.isCorrect()
        resOptionOne.getRelevance() == 1
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        resOptionTwo.isCorrect()
        resOptionTwo.getRelevance() == 2

    }

    def "update two options wtih same relevance"() {
        //exception
        given: "an question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        optionDto.setRelevance(1)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setRelevance(1)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TWO_OPTIONS_SAME_RELEVANCE
    }


    def "update options' relevance"(){
        given: "an question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        optionDto.setRelevance(1)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "relevances are updated"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.isCorrect()
        resOptionOne.getRelevance() == 1
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        resOptionTwo.isCorrect()
        resOptionTwo.getRelevance() == 2


    }


    def "update two orderable options to one"(){
        given: "an question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "two options"
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setRelevance(1)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setRelevance(2)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        def optionDto = new OptionDto(optionOK)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        optionDto.setRelevance(-1)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "questions is updated"
        questionRepository.count() == 2
        def result = questionRepository.findAll().get(1)
        !result.getQuestionDetails().isOrderableByRelevance()
        and: "are not changed"
        result.getId() == question.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() == null
        and: "an option is changed"
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.isCorrect()
        resOptionOne.getRelevance() == 1
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        resOptionTwo.isCorrect()
        resOptionTwo.getRelevance() == -1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }

}
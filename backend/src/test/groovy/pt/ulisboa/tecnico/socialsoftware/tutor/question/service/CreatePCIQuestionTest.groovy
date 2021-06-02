package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.PCIQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import spock.lang.Unroll

@DataJpaTest
class CreatePCIQuestionTest extends SpockTest{

    def "create an item combination question with two items on each side"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def pciQuestionDto = new PCIQuestionDto()

        PCIItemDto pciItemDto1 = new PCIItemDto()
        pciItemDto1.content = OPTION_1_CONTENT

        PCIItemDto pciItemDto2 = new PCIItemDto()
        pciItemDto2.content = OPTION_1_CONTENT

        PCIItemDto pciItemDto3 = new PCIItemDto()
        pciItemDto3.content = OPTION_1_CONTENT

        PCIItemDto pciItemDto4 = new PCIItemDto()
        pciItemDto4.content = OPTION_1_CONTENT

        pciQuestionDto.getItemGroupA().add(pciItemDto1)
        pciQuestionDto.getItemGroupA().add(pciItemDto2)

        pciQuestionDto.getItemGroupB().add(pciItemDto3)
        pciQuestionDto.getItemGroupB().add(pciItemDto4)

        questionDto.setQuestionDetailsDto(pciQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getItemGroupA().size() == 2
        result.getQuestionDetailsDto().getItemGroupB().size() == 2
        result.getQuestionDetailsDto().getItemGroupB().get(1).getContent() == OPTION_1_CONTENT
        result.getQuestionDetailsDto().getItemGroupB().get(0).getContent() == OPTION_1_CONTENT

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoCode = (PCIQuestion) repoResult.getQuestionDetails()
        repoCode.getItemGroupA().size() == 2
        repoCode.getItemGroupB().size() == 2
        def resItem1 = repoCode.getItemGroupA().get(0)
        def resItem2 = repoCode.getItemGroupB().get(0)
        resItem1.getContent() == OPTION_1_CONTENT
        resItem2.getContent() == OPTION_1_CONTENT
    }


    def "create an item combination question with no title"() {
        //exception thrown - title can't be empty
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(' ')
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def pciQuestionDto = new PCIQuestionDto()

        questionDto.setQuestionDetailsDto(pciQuestionDto)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "create an item combination question with no question (content)"() {
        //exception thrown - question can't be empty
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(' ')
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def pciQuestionDto = new PCIQuestionDto()

        questionDto.setQuestionDetailsDto(pciQuestionDto)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_QUESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
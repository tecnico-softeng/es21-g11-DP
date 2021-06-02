package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.apache.http.client.HttpResponseException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportMultipleChoiceQuestionWebServiceIT extends SpockTest{


    @LocalServerPort
    private int port

    def question
    def question2
    def optionOK
    def optionKO
    def optionOK2
    def optionKO2
    def teacher
    def course
    def courseExecution
    def questionDetails
    def questionDetails2

    def setup() {
        given: "a rest client"
        restClient = new RESTClient("http://localhost:" + port)

        and: "a course"
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: "multiple choice questions"
        question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        question2 = new Question()
        question2.setCourse(course)
        question2.setKey(2)
        question2.setTitle(QUESTION_2_TITLE)
        question2.setContent(QUESTION_2_CONTENT)
        question2.setStatus(Question.Status.AVAILABLE)
        question2.setNumberOfAnswers(2)
        question2.setNumberOfCorrect(1)
        questionDetails2 = new MultipleChoiceQuestion()
        question2.setQuestionDetails(questionDetails2)
        questionDetailsRepository.save(questionDetails2)
        questionRepository.save(question2)


        and: 'four options'
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

        optionOK2 = new Option()
        optionOK2.setContent(OPTION_1_CONTENT)
        optionOK2.setCorrect(true)
        optionOK2.setRelevance(-1)
        optionOK2.setSequence(0)
        optionOK2.setQuestionDetails(questionDetails2)
        optionRepository.save(optionOK2)

        optionKO2 = new Option()
        optionKO2.setContent(OPTION_1_CONTENT)
        optionKO2.setCorrect(false)
        optionKO2.setRelevance(-1)
        optionKO2.setSequence(1)
        optionKO2.setQuestionDetails(questionDetails2)
        optionRepository.save(optionKO2)


    }

    def "export a multiple choice questions as teacher"() {

        given: "a teacher login"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def questionDto2 = new QuestionDto(question)
        questionDto2.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        optionDto = new OptionDto(optionOK2)
        options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO2)
        options.add(optionDto)
        questionDto2.getQuestionDetailsDto().setOptions(options)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when: "the web service is invoked"
        def map = restClient.get(
                path: '/courses/'+ course.getId() +'/questions/export/',
                requestContentType: 'application/json'
        )
        then: "the request returns ok"
        assert map['response'].status == 200
        assert map['reader'] != null


        cleanup:
        userRepository.deleteById(teacher.getId())
    }


    def cleanup() {
        persistentCourseCleanup()

        questionDetailsRepository.deleteById(questionDetails.getId())
        questionDetailsRepository.deleteById(questionDetails2.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}

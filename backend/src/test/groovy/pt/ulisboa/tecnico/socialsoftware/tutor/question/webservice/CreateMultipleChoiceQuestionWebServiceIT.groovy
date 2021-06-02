package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovy.json.JsonOutput
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
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
class CreateMultipleChoiceQuestionWebServiceIT extends SpockTest{


    @LocalServerPort
    private int port

    def response
    def question
    def teacher
    def course
    def courseExecution

    def setup() {
        given: "a rest client"
        restClient = new RESTClient("http://localhost:" + port)


        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)


        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "create a multiple choice question as teacher"(){


        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: "the web service is invoked"
        response = restClient.post(
                path: '/courses/' + course.getId() + '/questions',
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns ok"
        response.status == 200
        and: "the response contains the right multiple choice question"
        def question = response.data
        question.id != null
        question.title == QUESTION_1_TITLE
        question.content == QUESTION_1_CONTENT
        question.questionDetailsDto.orderableByRelevance == false
        question.questionDetailsDto.options.size() == 2
        question.questionDetailsDto.options.get(0).correct
        question.questionDetailsDto.options.get(0).content == OPTION_1_CONTENT
        question.questionDetailsDto.options.get(1).correct == false
        question.questionDetailsDto.options.get(1).content == OPTION_2_CONTENT

        and: "the database contains the right multiple choice question"
        def questionDb = questionRepository.findAll().get(0)
        questionDb.getId() == question.id
        questionDb != null
        questionDb.getTitle() == QUESTION_1_TITLE
        questionDb.content == QUESTION_1_CONTENT
        questionDb.getQuestionDetails().isOrderableByRelevance() == false
        questionDb.getQuestionDetails().getOptions().size() == 2
        questionDb.getQuestionDetails().getOptions().get(0).isCorrect()
        questionDb.getQuestionDetails().getOptions().get(0).getContent() == OPTION_1_CONTENT
        questionDb.getQuestionDetails().getOptions().get(1).isCorrect() == false
        questionDb.getQuestionDetails().getOptions().get(1).getContent() == OPTION_2_CONTENT

    }


    def "can't create a multiple choice question with only false options"() {

        given: "a wrong questionDto (only false options)"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: "the web service is invoked"
        response = restClient.post(
                path: '/courses/' + course.getId() + '/questions',
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 405"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_BAD_REQUEST
    }


    def "teacher not in course can't create question"() {

        given: "a teacher not in the course"
        teacher.getCourseExecutions().remove(courseExecution)
        courseExecution.getTeachers().remove(teacher)
        userRepository.save(teacher)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: "the web service is invoked"
        response = restClient.post(
                path: '/courses/' + course.getId() + '/questions',
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

    }



    def cleanup() {

        persistentCourseCleanup()

        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())

    }
}

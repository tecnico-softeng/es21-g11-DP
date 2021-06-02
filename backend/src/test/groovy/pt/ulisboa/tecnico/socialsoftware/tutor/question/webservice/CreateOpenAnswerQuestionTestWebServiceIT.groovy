package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateOpenAnswerQuestionTestWebServiceIT extends SpockTest{
    @LocalServerPort
    private int port

    def response
    def question
    def teacher
    def course
    def courseExecution
    def correctAnswer

    def setup() {
        correctAnswer = CORRECT_ANSWER_1_QUESTION

        given: "a rest client"
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM,
                Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }


    def "create an open answer question as teacher"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        and: "a correct answer"
        def openAnswer = new OpenAnswerQuestionDto()
        openAnswer.setCorrectAnswer(CORRECT_ANSWER_1_QUESTION)
        questionDto.setQuestionDetailsDto(openAnswer)

        when: "the web service is invoked"
        def objectMapper = new ObjectMapper()
        response = restClient.post(
                path: '/courses/' + course.getId() + '/questions',
                body: objectMapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns ok"
        response != null
        response.status == 200

        and: "the response contains the right open answer question"
        def question = response.data
        question.id != null
        question.title == questionDto.getTitle()
        question.content == questionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.correctAnswer == correctAnswer

        and: "the database contains the right multiple choice question"
        def questionDb = questionRepository.findAll().get(0)
        questionDb.getId() == question.id
        questionDb != null
        questionDb.getTitle() == QUESTION_1_TITLE
        questionDb.content == QUESTION_1_CONTENT
        questionDb.questionDetailsDto.correctAnswer == correctAnswer
    }


    def "teacher not in course can't create question"() {
        given: "a teacher not in the course"
        teacher.getCourseExecutions().remove(courseExecution)
        courseExecution.getTeachers().remove(teacher)
        userRepository.save(teacher)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        and: "a correct answer"
        //questionDto.getQuestionDetailsDto().setCorrectAnswer(correctAnswer)

        when: "the web service is invoked"
        def objectMapper = new ObjectMapper()
        response = restClient.post(
                path: '/courses/' + course.getId() + '/questions',
                body: objectMapper.writeValueAsString(questionDto),
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
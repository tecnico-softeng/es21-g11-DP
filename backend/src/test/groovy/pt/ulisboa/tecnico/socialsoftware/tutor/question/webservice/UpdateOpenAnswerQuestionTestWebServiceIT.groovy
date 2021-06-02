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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateOpenAnswerQuestionTestWebServiceIT extends SpockTest{
    @LocalServerPort
    private int port

    def response
    def question
    def course
    def courseExecution
    def questionDetails

    def setup() {
        given: "a rest client"
        restClient = new RESTClient("http://localhost:" + port)

        and: "a course"
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM,
                Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: "an open answer question with a correct answer"
        question = new Question()
        question.setCourse(course)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)

        questionDetails = new OpenAnswerQuestion()
        questionDetails.setCorrectAnswer(CORRECT_ANSWER_1_QUESTION)

        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
    }


    def "update an open answer question"(){
        given: "a teacher login"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        questionService.createQuestion(courseExecution.getId(), questionDto)

        when: "the web service is invoked"
        response = restClient.put(
                path: '/questions/' + question.getId() + '/',
                body: questionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns ok"
        response != null
        response.status == 200

        and: "the response contains correct data"
        def info = response.data
        info.content == QUESTION_1_CONTENT

        and : "the DB contains the updated info"
        def result = questionRepository.findAll().get(0)
        result.getId() == info.id
        result.getContent() == QUESTION_1_CONTENT

        cleanup:
        userRepository.deleteById(teacher.getId())
    }


    def "update an open answer question as a student"(){
        given: "a student login"
        def student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        userRepository.save(student)

        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        when: "the web service is invoked"
        response = restClient.put(
                path: '/questions/' + question.getId() + '/',
                body: questionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns access denied 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        userRepository.deleteById(student.getId())
    }


    def "update an open answer question as teacher not in courseExectution"() {
        given: "a teacher login"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        when: "the web service is invoked"
        response = restClient.put(
                path: '/questions/' + question.getId() + '/',
                body: questionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns access denied 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        userRepository.deleteById(teacher.getId())
    }


    def "update an open answer question with no content"() {
        given: "a teacher login"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a new question without content"
        def newQuestion = new Question()
        newQuestion.setCourse(course)
        newQuestion.setTitle(QUESTION_2_TITLE)
        newQuestion.setStatus(Question.Status.AVAILABLE)

        def newQuestionDetails = new OpenAnswerQuestion()
        newQuestionDetails.setCorrectAnswer(CORRECT_ANSWER_1_QUESTION)
        newQuestion.setQuestionDetails(newQuestionDetails)
        questionDetailsRepository.save(newQuestionDetails)

        def questionDto = new QuestionDto(newQuestion)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        when: "the web service is invoked"
        response = restClient.put(
                path: '/questions/' + newQuestion.getId() + '/',
                body: questionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns access denied 400"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_BAD_REQUEST

        cleanup:
        questionDetailsRepository.deleteById(newQuestionDetails.getId())
        userRepository.deleteById(teacher.getId())
    }



    def cleanup() {
        persistentCourseCleanup()

        questionDetailsRepository.deleteById(questionDetails.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}

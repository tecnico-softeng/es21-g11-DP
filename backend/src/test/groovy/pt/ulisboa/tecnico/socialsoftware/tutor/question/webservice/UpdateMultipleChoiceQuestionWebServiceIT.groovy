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
class UpdateMultipleChoiceQuestionWebServiceIT extends SpockTest{


    @LocalServerPort
    private int port

    def response
    def question
    def optionOK
    def optionKO
    def teacher
    def student
    def course
    def courseExecution
    def questionDetails

    def setup() {
        given: "a rest client"
        restClient = new RESTClient("http://localhost:" + port)

        and: "a course"
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: "a multiple choice question"
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

    def "update a multiple choice question as teacher"(){
        given: "a teacher login"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        def optionDto = new OptionDto(optionOK)
        def options = new ArrayList<OptionDto>()
        optionDto.setRelevance(2)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setRelevance(1)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        info.questionDetailsDto.orderableByRelevance == true
        def optionOne = info.questionDetailsDto.options.stream().filter({ option -> option.id == optionOK.id }).findAny().orElse(null)
        optionOne.correct
        optionOne.relevance == 2
        def optionTwo = info.questionDetailsDto.options.stream().filter({ option -> option.id == optionKO.id }).findAny().orElse(null)
        optionTwo.correct
        optionTwo.relevance == 1

        and : "the DB contains the updated info"
        def result = questionRepository.findAll().get(0)

        result.getId() == info.id
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetails().isOrderableByRelevance()
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.isCorrect()
        resOptionOne.getRelevance() == 2
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        resOptionTwo.isCorrect()
        resOptionTwo.getRelevance() == 1

        cleanup:
        userRepository.deleteById(teacher.getId())
    }


    def "update a multiple choice question as a student"(){
        given: "a student login"
        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        userRepository.save(student)

        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        def optionDto = new OptionDto(optionOK)
        def options = new ArrayList<OptionDto>()
        optionDto.setRelevance(2)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setRelevance(1)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)



        when: "the web service is invoked"
        response = restClient.put(
                path: '/questions/' + question.getId() + '/',
                body: questionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns access denied 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        and : "the DB contains the old info"
        def result = questionRepository.findAll().get(0)

        result.getId()  == question.getId()
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.isCorrect()
        resOptionOne.getRelevance() == -1
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        !resOptionTwo.isCorrect()
        resOptionTwo.getRelevance() == -1

        cleanup:
        userRepository.deleteById(student.getId())
    }


    def "update a multiple choice question as teacher not in courseExectution"(){
        given: "a teacher login"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setNumberOfCorrect(2)
        def optionDto = new OptionDto(optionOK)
        def options = new ArrayList<OptionDto>()
        optionDto.setRelevance(2)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setRelevance(1)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: "the web service is invoked"
        response = restClient.put(
                path: '/questions/' + question.getId() + '/',
                body: questionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns access denied 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        and : "the DB contains the old info"
        def result = questionRepository.findAll().get(0)

        result.getId()  == question.getId()
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.isCorrect()
        resOptionOne.getRelevance() == -1
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        !resOptionTwo.isCorrect()
        resOptionTwo.getRelevance() == -1

        cleanup:
        userRepository.deleteById(teacher.getId())
    }


    def "update a multiple choice question with no correct options"() {
        given: "a teacher login"
        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        def optionDto = new OptionDto(optionOK)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        optionDto.setRelevance(2)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setRelevance(1)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: "the web service is invoked"
        response = restClient.put(
                path: '/questions/' + question.getId() + '/',
                body: questionDto,
                requestContentType: 'application/json'
        )

        then: "the request returns 405"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_BAD_REQUEST

        and : "the DB contains the old info"
        def result = questionRepository.findAll().get(0)

        result.getId()  == question.getId()
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId() }).findAny().orElse(null)
        resOptionOne.isCorrect()
        resOptionOne.getRelevance() == -1
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId() }).findAny().orElse(null)
        !resOptionTwo.isCorrect()
        resOptionTwo.getRelevance() == -1

        cleanup:
        userRepository.deleteById(teacher.getId())
    }

        def cleanup() {
        persistentCourseCleanup()

        questionDetailsRepository.deleteById(questionDetails.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}

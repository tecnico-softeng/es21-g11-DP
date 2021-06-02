package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PCIItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PCIQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemovePCIQuestionWebServiceIT extends SpockTest{


    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def question
    def response

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
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new PCIQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def itemDto1 = new PCIItemDto()
        def itemDto2 = new PCIItemDto()
        def itemDto3 = new PCIItemDto()
        itemDto1.setContent(OPTION_1_CONTENT)
        itemDto2.setContent(OPTION_2_CONTENT)
        itemDto3.setContent(OPTION_3_CONTENT)

        def itemDto1copy = new PCIItemDto()
        def itemDto2copy = new PCIItemDto()
        def itemDto3copy = new PCIItemDto()
        itemDto1copy.setContent(OPTION_1_CONTENT)
        itemDto2copy.setContent(OPTION_2_CONTENT)
        itemDto3copy.setContent(OPTION_3_CONTENT)

        def item1Corresponding = new ArrayList<PCIItemDto>()
        def item2Corresponding = new ArrayList<PCIItemDto>()
        def item3Corresponding = new ArrayList<PCIItemDto>()

        item1Corresponding.add(itemDto3copy)
        item2Corresponding.add(itemDto3copy)
        item3Corresponding.add(itemDto1copy)
        item3Corresponding.add(itemDto2copy)

        itemDto1.setCorresponding(item1Corresponding)
        itemDto2.setCorresponding(item2Corresponding)
        itemDto3.setCorresponding(item3Corresponding)

        def itemsGroupA = new ArrayList<PCIItemDto>()
        itemsGroupA.add(itemDto1)
        itemsGroupA.add(itemDto2)

        def itemsGroupB = new ArrayList<PCIItemDto>()
        itemsGroupB.add(itemDto3)

        questionDto.getQuestionDetailsDto().setItemGroupA(itemsGroupA)
        questionDto.getQuestionDetailsDto().setItemGroupB(itemsGroupB)

        questionService.createQuestion(course.getId(), questionDto)

        question = questionRepository.findAll().get(0)


        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "remove a pci question as teacher"(){

        when: "the web service is invoked"
        response = restClient.delete(
                path: '/questions/' + question.getId(),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
    }

    def "remove a pci question with no access to course"() {
        given: "a teacher not in the course"
        teacher.getCourseExecutions().remove(courseExecution)
        courseExecution.getTeachers().remove(teacher)
        userRepository.save(teacher)

        when: "the web service is invoked"
        response = restClient.delete(
                path: '/questions/' + question.getId(),
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
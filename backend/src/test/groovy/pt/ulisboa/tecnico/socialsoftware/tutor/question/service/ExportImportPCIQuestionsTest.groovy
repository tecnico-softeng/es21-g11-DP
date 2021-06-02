package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class ExportImportPCIQuestionsTest extends SpockTest{

//    def 'export questions to xml'() {
//        expect: false
//
//    }
//
//    def 'import questions to xml'() {
//        expect: false
//
//    }
//
//    def 'export to latex'() {
//        expect: false
//    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenAnswerStatementQuestionDetails extends StatementQuestionDetails {
  correctAnswer: String = '';

  constructor(jsonObj: OpenAnswerStatementQuestionDetails) {
    super(QuestionTypes.OpenAnswer);

    if (jsonObj) {
      if (jsonObj.correctAnswer) {
        this.correctAnswer = jsonObj.correctAnswer;
      }
    }
  }
}

import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenAnswerAnswerType extends AnswerDetails {
  correctAnswer: String = ''; //Teacher Correct Answer

  constructor(jsonObj?: OpenAnswerAnswerType) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      console.log('in OpenAnswerAnswerType constructor');
      console.log(jsonObj);
      this.correctAnswer = jsonObj.correctAnswer;
    }
  }

  isCorrect(): boolean {
    return true;
  }

  answerRepresentation(): string {
    return this.correctAnswer.toString();
  }
}

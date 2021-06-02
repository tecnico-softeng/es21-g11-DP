import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenAnswerQuestionDetails extends QuestionDetails {
  correctAnswer: String = '';

  constructor(jsonObj?: OpenAnswerQuestionDetails) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      this.correctAnswer = '';
    }
  }

  setCorrectAnswer(newCorrectAnswer: String) {
    this.correctAnswer = newCorrectAnswer;
  }

  getCorrectAnswer() {
    return this.correctAnswer;
  }

  setAsNew(): void {}
}

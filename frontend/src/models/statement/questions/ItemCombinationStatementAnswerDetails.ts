import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import MultipleChoiceStatementCorrectAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementCorrectAnswerDetails';

export default class ItemCombinationStatementAnswerDetails extends StatementAnswerDetails {
  public optionId: number | null = null;

  constructor(jsonObj?: ItemCombinationStatementAnswerDetails) {
    super(QuestionTypes.MultipleChoice);
  }

  isQuestionAnswered(): boolean {
    return false;
  }

  isAnswerCorrect(
    correctAnswerDetails: ItemCombinationStatementAnswerDetails
  ): boolean {
    return false;
  }
}

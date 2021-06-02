import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctOptionId: number | null = null;

  constructor(jsonObj?: ItemCombinationStatementCorrectAnswerDetails) {
    super(QuestionTypes.MultipleChoice);
  }
}

import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import StatementOption from '@/models/statement/StatementOption';
import { _ } from 'vue-underscore';

export default class ItemCombinationStatementQuestionDetails extends StatementQuestionDetails {
  items: StatementOption[] = [];

  constructor(jsonObj?: ItemCombinationStatementQuestionDetails) {
    super(QuestionTypes.MultipleChoice);
  }
}

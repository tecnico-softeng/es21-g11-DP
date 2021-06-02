import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';
import PCIItem from '@/models/management/questions/PCIItem';

export default class ItemCombinationAnswerDetails extends AnswerDetails {
  item!: PCIItem;

  constructor(jsonObj?: ItemCombinationAnswerDetails) {
    super(QuestionTypes.ItemCombination);
  }

  isCorrect(): boolean {
    return false;
  }
  answerRepresentation(): string {
    return '';
  }
}

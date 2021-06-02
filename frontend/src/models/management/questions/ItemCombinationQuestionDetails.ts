import PCIItem from '@/models/management/questions/PCIItem';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationQuestionDetails extends QuestionDetails {
  itemGroupA: PCIItem[] = [new PCIItem(), new PCIItem()];
  itemGroupB: PCIItem[] = [new PCIItem(), new PCIItem()];

  constructor(jsonObj?: ItemCombinationQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.itemGroupA = jsonObj.itemGroupA
        ? jsonObj.itemGroupA.map(
          (item: PCIItem) => new PCIItem(item)
        )
        : this.itemGroupA;
      this.itemGroupB = jsonObj.itemGroupB
        ? jsonObj.itemGroupB.map(
          (item: PCIItem) => new PCIItem(item)
        )
        : this.itemGroupB;
    }
  }

  setAsNew(): void {
    this.itemGroupA.forEach(item => {
      item.id = null;
    });
    this.itemGroupB.forEach(item => {
      item.id = null;
    });
  }
}

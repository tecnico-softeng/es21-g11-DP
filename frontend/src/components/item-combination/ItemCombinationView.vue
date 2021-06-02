<template>
  <ul>
    <li v-for="itemA in questionDetails.itemGroupA" :key="itemA.id">
      <span
        v-for="itemB in itemA.corresponding" :key="itemB.id"
        v-html="
          convertMarkDown(
          itemA.content + ' |  Corresponding: ' + itemB.content
          )
        "
      />
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import ItemCombinationAnswerDetails from '@/models/management/questions/ItemCombinationAnswerDetails';

@Component
export default class MultipleChoiceView extends Vue {
  @Prop() readonly questionDetails!: ItemCombinationQuestionDetails;
  @Prop() readonly answerDetails?: ItemCombinationAnswerDetails;

  studentAnswered(option: number) {
    return this.answerDetails && this.answerDetails?.item.id === option
      ? '**[S]** '
      : '';
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

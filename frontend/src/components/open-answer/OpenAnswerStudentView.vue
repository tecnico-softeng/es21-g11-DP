<!--
Used on:
   - ResultComponent.vue
-->

<template>
  <div data-cy="'studentAnswer'" class="student-answer-container">
    <v-list :label="'Student Answer'" :data-cy="'studentAnswer'">
      <span>{{ answerDetails.studentAnswer }}</span>
    </v-list>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import OpenAnswerStatementQuestionDetails from '@/models/statement/questions/OpenAnswerStatementQuestionDetails';
import OpenAnswerStatementAnswerDetails from '@/models/statement/questions/OpenAnswerStatementAnswerDetails';
import OpenAnswerStatementCorrectAnswerDetails from '@/models/statement/questions/OpenAnswerStatementCorrectAnswerDetails';

@Component
export default class OpenAnswerStudentView extends Vue {
  @Prop(OpenAnswerStatementAnswerDetails)
  readonly answerDetails!: OpenAnswerStatementAnswerDetails;
}
</script>
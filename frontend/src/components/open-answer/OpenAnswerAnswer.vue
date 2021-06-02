<!--
Used on:
  - QuestionComponent.vue
-->

<template>
  <div>
    <v-textarea
      v-model="answerDetails.studentAnswer"
      :label="'Student Answer'"
      :data-cy="'studentAnswer'"
      rows="2"
      auto-grow
    >
    </v-textarea>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import OpenAnswerStatementAnswerDetails from '@/models/statement/questions/OpenAnswerStatementAnswerDetails';

@Component
export default class OpenAnswerAnswer extends Vue {
  @Prop(OpenAnswerStatementAnswerDetails)
  answerDetails!: OpenAnswerStatementAnswerDetails;
}
</script>

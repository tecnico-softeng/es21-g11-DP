<!--
Used on:
  - EditQuestionSubmissionDialog.vue
  - EditQuestionDialog.vue
-->
<template>
  <div class="open-answer-question">
    <v-textarea
      v-model="sQuestionDetails.correctAnswer"
      :label="'Correct Answer'"
      :data-cy="'correctAnswer'"
      rows="2"
      auto-grow
    >
    </v-textarea>
  </div>
</template>

<script lang="ts">
import { Component, PropSync, Vue } from 'vue-property-decorator';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';

@Component
export default class OpenAnswerCreate extends Vue {
  @PropSync('questionDetails', { type: OpenAnswerQuestionDetails })
  sQuestionDetails!: OpenAnswerQuestionDetails;
}
</script>

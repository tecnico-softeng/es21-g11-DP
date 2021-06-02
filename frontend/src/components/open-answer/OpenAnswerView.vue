<!--
Used on:
  - ShowQuestion.vue
-->
<template>
  <div class="open-answer-question">
    <v-textarea
      v-model="questionDetails.correctAnswer"
      :label="'Teacher Correct Answer'"
      :data-cy="'correctAnswer'"
      rows="1"
      auto-grow
    >
    </v-textarea>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';

@Component
export default class OpenAnswerView extends Vue {
  @Prop() readonly questionDetails!: OpenAnswerQuestionDetails;
}
</script>

<template>
  <div class="item-combination-items">

    <v-row
      v-for="(item, index) in sQuestionDetails.itemGroupA"
      :key="'A'+index"
      data-cy="questionItemsInput"
    >
      <v-col cols="10">
        <v-textarea
          v-model="item.content"
          :label="`A Item ${index + 1}`"
          :data-cy="`Item${index + 1}`"
          rows="1"
          auto-grow
        ></v-textarea>
      </v-col>
      <v-col v-if="sQuestionDetails.itemGroupA.length > 1">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              :data-cy="`Delete${index + 1}`"
              small
              class="ma-1 action-button"
              v-on="on"
              @click="removeItemA(index)"
              color="red"
              >close</v-icon
            >
          </template>
          <span>Remove Item</span>
        </v-tooltip>
      </v-col>
    </v-row>

    <v-row
        v-for="(item, index) in sQuestionDetails.itemGroupB"
        :key="'B'+index"
        data-cy="questionItemsInput"
    >
      <v-col cols="10">
        <v-textarea
            v-model="item.content"
            :label="`B Item ${index + 1}`"
            :data-cy="`Item${index + 1}`"
            rows="1"
            auto-grow
        ></v-textarea>
      </v-col>
      <v-col v-if="sQuestionDetails.itemGroupB.length > 1">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
                :data-cy="`Delete${index + 1}`"
                small
                class="ma-1 action-button"
                v-on="on"
                @click="removeItemB(index)"
                color="red"
            >close</v-icon
            >
          </template>
          <span>Remove Item</span>
        </v-tooltip>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="6">
        <v-btn
            class="ma-auto"
            color="blue darken-1"
            @click="addItemA"
            data-cy="addItemCombinationItem"
        >Add Item Group A</v-btn
        >
      </v-col>
      <v-col cols="6">
        <v-btn
            class="ma-auto"
            color="blue darken-1"
            @click="addItemB"
            data-cy="addItemCombinationItem"
        >Add Item Group B</v-btn
        >
      </v-col>
    </v-row>

    <v-container fluid>
      <v-row
          align="center"
          v-for="(itemA, index) in sQuestionDetails.itemGroupA"
          :key="index"
          data-cy="questionItemsInput"
      >
        <v-col
            cols="12"
            sm="6"
        ><v-subheader v-text="itemA.content"></v-subheader>
        </v-col>

        <v-col
            cols="12"
            sm="6"
        >
          <v-select
              v-model="selected[index]"
              :items="groupBContent"
              value="selected[index]"
              label="Group B items"
              multiple
              chips
              hint="Select the corresponding items"
              persistent-hint
              @input='addCorresponding(index)'
          ></v-select>
        </v-col>
      </v-row>
    </v-container>

  </div>
</template>

<script lang="ts">
import { Component, Model, Prop, PropSync, Vue, Watch } from 'vue-property-decorator';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import PCIItem from '@/models/management/questions/PCIItem';

@Component
export default class ItemCombinationCreate extends Vue {
  @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
  sQuestionDetails!: ItemCombinationQuestionDetails;
  selected: String[][] = [];

  mounted() {
    for (let i = 0; i < this.sQuestionDetails.itemGroupA.length; i++) {
      this.selected[i] = [];
      for (let j = 0; j < this.sQuestionDetails.itemGroupA[i].corresponding.length; j++) {
        this.selected[i][j] = this.sQuestionDetails.itemGroupA[i].corresponding[j].content;
      }
    }
    this.$forceUpdate();
  }

  addItemA() {
    this.sQuestionDetails.itemGroupA.push(new PCIItem());
  }

  addItemB() {
    this.sQuestionDetails.itemGroupB.push(new PCIItem());
  }

  removeItemA(index: number) {
    if (!(this.sQuestionDetails.itemGroupA.length == index + 1)) {
      this.selected.splice(index, 1);
    }
    this.sQuestionDetails.itemGroupA.splice(index, 1);
  }

  removeItemB(index: number) {
    this.sQuestionDetails.itemGroupB.splice(index, 1);
  }

  get groupBContent(): String[] {
    let groupBContents: String[] = [];
    this.sQuestionDetails.itemGroupB.forEach(function(item) {groupBContents.push(item.content)});
    return groupBContents;
  }

  addCorresponding(index: number) {
    this.sQuestionDetails.itemGroupA[index].corresponding = [];
    let questionDetails = this.sQuestionDetails;

    this.selected[index].forEach(function(itemBcontent) {
          for (let i = 0; i < questionDetails.itemGroupB.length; i++) {
            if (questionDetails.itemGroupB[i].content === itemBcontent) {
              questionDetails.itemGroupA[index].corresponding.push(questionDetails.itemGroupB[i]);
              break;
            }
          }
        }
    );
  }
}
</script>

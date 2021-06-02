export default class Option {
  id: number | null = null;
  sequence!: number | null;
  relevance: number = -1;
  content: string = '';
  correct: boolean = false;

  constructor(jsonObj?: Option) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.correct = true;
      this.relevance = jsonObj.relevance;
    }
  }
}

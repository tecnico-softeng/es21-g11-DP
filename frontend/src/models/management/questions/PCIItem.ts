export default class PCIItem {
  id: number | null = null;
  content: string = '';
  corresponding: PCIItem[] = [];

  constructor(jsonObj?: PCIItem) {
    if (jsonObj) {
      this.id = jsonObj.id || this.id;
      this.content = '';
      this.corresponding = jsonObj.corresponding
        ? jsonObj.corresponding.map((item: PCIItem) => new PCIItem(item))
        : this.corresponding;
    }
  }
}
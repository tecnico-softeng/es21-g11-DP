package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PCIItemDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "combination_items")
public class PCIItem implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PCIItem> corresponding = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_details_id")
    private PCIQuestion questionDetails;

    public PCIItem() {
    }

    public PCIItem(PCIItemDto pciItemDto) {
        setCorresponding(pciItemDto.getCorresponding());
        setContent(pciItemDto.getContent());
    }

    public PCIItem(PCIItemDto pciItemDto, List<PCIItem> itemGroupB) {
        setCorresponding(pciItemDto.getCorresponding(), itemGroupB);
        setContent(pciItemDto.getContent());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<PCIItem> getCorresponding() {
        return corresponding;
    }

    public void setCorresponding(List<PCIItemDto> corresponding) {
        for (PCIItemDto itemDto : corresponding) {
            PCIItem pciItem = new PCIItem(itemDto);
            this.corresponding.add(pciItem);
        }
    }

    public void setCorresponding(List<PCIItemDto> corresponding, List<PCIItem> itemGroupB) {
        this.corresponding.clear();
        for (PCIItemDto itemDto : corresponding) {
            for (PCIItem item: itemGroupB) {
                if (itemDto.getContent().equals(item.getContent())) {
                    PCIItem pciItem = item;
                    this.corresponding.add(pciItem);
                }
            }
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PCIQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(PCIQuestion questionDetails) {
        this.questionDetails = questionDetails;
    }

    public void delete() {
        this.questionDetails = null;
    }

    public void visitItems(Visitor visitor) {
        for (PCIItem item : this.getCorresponding()) {
            item.accept(visitor);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitPCIItem(this);
    }
}

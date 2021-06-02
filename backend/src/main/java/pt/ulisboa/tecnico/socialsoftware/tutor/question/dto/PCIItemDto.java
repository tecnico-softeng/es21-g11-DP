package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.PCIItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PCIItemDto implements Serializable {
    private Integer id;
    private String content;
    private List<PCIItemDto> corresponding = new ArrayList<>();


    public PCIItemDto() {
    }

    public PCIItemDto(PCIItem pciItem) {
        this.id = pciItem.getId();
        this.content = pciItem.getContent();
        this.corresponding = pciItem.getCorresponding().stream().map(PCIItemDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PCIItemDto> getCorresponding() {
        return corresponding;
    }

    public void setCorresponding(List<PCIItemDto> corresponding) {
        this.corresponding = corresponding;
    }

    @Override
    public String toString() {
        return "PCIItemDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", corresponding=" + corresponding +
               '}';
    }
}

package XsdElements;

import java.util.*;

public abstract class XsdMultipleElements extends XsdElementBase {

    private List<XsdElement> elements = new ArrayList<>();
    private Map<String, List<XsdElement>> groupElements = new HashMap<>();
    private List<XsdGroup> unsolvedGroupElements = new ArrayList<>();

    void replaceElement(int elementIndex, XsdElement newElement){
        elements.set(elementIndex, newElement);
    }

    void addElement(XsdElement element){
        this.elements.add(element);
    }

    void addElements(List<XsdElement> elements){
        this.elements.addAll(elements);
    }

    void addGroup(String groupName, List<XsdElement> elements){
        groupElements.put(groupName, elements);
    }

    public Map<String, List<XsdElement>> getGroupElements(){
        return groupElements;
    }

    void addUnsolvedGroup(XsdGroup group){
        unsolvedGroupElements.add(group);
    }

    void removeUnsolvedGroup(XsdGroup group) {
        unsolvedGroupElements.remove(group);
    }

    List<XsdGroup> getUnresolvedGroups(){
        return unsolvedGroupElements;
    }

    public List<XsdElement> getElements(){
        return elements;
    }

}

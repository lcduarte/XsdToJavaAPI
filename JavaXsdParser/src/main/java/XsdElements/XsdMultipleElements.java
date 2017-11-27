package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;

import java.util.*;

public abstract class XsdMultipleElements extends XsdElementBase {

    /**
     * This Map contains the separated elements from XsdGroups. This way this class's elements
     * are divided into direct children, represented by the List elements and shared children,
     * represented by this Map. This way it simplifies the division of methods that will belong
     * to an interface and those that will be contained in the elements class.
     */
    private Map<String, List<ReferenceBase>> groupElements = new HashMap<>();

    /**
     * The elements List is a flattened list of all the children of a given XsdMultipleElement object
     * that aren't a xsd:group.
     */
    private List<ReferenceBase> elements = new ArrayList<>();

    void addElement(ReferenceBase element){
        this.elements.add(element);
    }

    void addElements(List<ReferenceBase> elements){
        this.elements.addAll(elements);
    }

    public Map<String, List<ReferenceBase>> getGroupElements(){
        return groupElements;
    }

    @Override
    public void baseRefChange(ConcreteElement elementWrapper) {
        if (elementWrapper.getElement() instanceof XsdElement){
            super.baseRefChange(elementWrapper);
        }

        if (elementWrapper.getElement() instanceof XsdGroup){
            groupElements.put(elementWrapper.getName(), elementWrapper.getElement().getElements());
        }
    }

    @Override
    public List<ReferenceBase> getElements(){
        return elements;
    }

}

package XsdElements.ElementsWrapper;

import XsdElements.XsdElementBase;

/**
 * ConcreteElement is a wrapper class for XsdElementBase which is fully resolved
 */
public class ConcreteElement extends ReferenceBase {

    private String name;
    private XsdElementBase element;

    ConcreteElement(XsdElementBase element){
        this.name = getName(element);
        this.element = element;
    }

    public String getName() {
        return name;
    }

    public XsdElementBase getElement() {
        return element;
    }

}

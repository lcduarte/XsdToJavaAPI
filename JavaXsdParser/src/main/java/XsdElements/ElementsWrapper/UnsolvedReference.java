package XsdElements.ElementsWrapper;

import XsdElements.XsdReferenceElement;

public class UnsolvedReference extends ReferenceBase {

    private String ref;
    private final XsdReferenceElement element;

    public UnsolvedReference(XsdReferenceElement element){
        this.ref = getRef(element);
        this.element = element;
    }

    public String getRef() {
        return ref;
    }

    public ReferenceBase getParent() {
        return ReferenceBase.createFromXsd(element.getParent());
    }

    public XsdReferenceElement getElement() {
        return element;
    }
}

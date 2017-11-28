package XsdElements.ElementsWrapper;

import XsdElements.XsdElement;
import XsdElements.XsdReferenceElement;

public class UnsolvedReference extends ReferenceBase {

    private String ref;
    private final XsdReferenceElement element;

    public UnsolvedReference(XsdReferenceElement element){
        this.ref = getRef(element);
        this.element = element;
    }

    public UnsolvedReference(String refType, XsdElement element){
        this.ref = refType;
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

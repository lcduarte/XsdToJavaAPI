package XsdElements.ElementsWrapper;

import XsdElements.XsdElement;
import XsdElements.XsdElementBase;
import XsdElements.XsdReferenceElement;

/**
 * UnsolvedReference is a wrapper class for all XsdReferenceElements which should be resolved
 * in the parsing process, if possible.
 */
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

    public XsdElementBase getParent() {
        return element.getParent();
    }

    public XsdReferenceElement getElement() {
        return element;
    }
}
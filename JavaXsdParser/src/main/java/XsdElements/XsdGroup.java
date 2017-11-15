package XsdElements;

import org.w3c.dom.Node;

public class XsdGroup extends XsdReferenceElement {

    private XsdMultipleElements childElement;

    public XsdGroup(Node node) {
        super(node);
}

    public void setChildElement(XsdMultipleElements childElement) {
        this.childElement = childElement;
    }

    public XsdMultipleElements getChildElement() {
        return childElement;
    }

}

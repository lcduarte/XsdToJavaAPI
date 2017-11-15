package XsdElements;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class XsdAttributeGroup extends XsdReferenceElement {

    private List<XsdAttribute> attributes = new ArrayList<>();

    public XsdAttributeGroup(Node node) {
        super(node);
    }

    public void addAttributes(XsdAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void addAttributes(List<XsdAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    public List<XsdAttribute> getAttributes() {
        return attributes;
    }
}

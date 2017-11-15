package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XsdAttribute extends XsdReferenceElement {

    // TODO public XsdSimpleType simpleType;

    public String defaultElement;
    public String fixed;
    public String type;

    public XsdAttribute(Node node) {
        super(node);

        NamedNodeMap attributes = node.getAttributes();

        this.type = attributes.getNamedItem("type") == null ? null : attributes.getNamedItem("type").getNodeValue();
    }

    public String getType() {
        return type;
    }
}

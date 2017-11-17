package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XsdAttribute extends XsdReferenceElement {

    // TODO public XsdSimpleType simpleType;
    public static final String TAG = "xsd:attribute";

    private String defaultElement;
    private String fixed;
    private String type;

    @Override
    public void setAttributes(NamedNodeMap attributes) {
        super.setAttributes(attributes);

        this.defaultElement = attributes.getNamedItem("defaultElement") == null ? null : attributes.getNamedItem("defaultElement").getNodeValue();
        this.fixed = attributes.getNamedItem("fixed") == null ? null : attributes.getNamedItem("fixed").getNodeValue();
        this.type = attributes.getNamedItem("type") == null ? null : attributes.getNamedItem("type").getNodeValue();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Visitor getVisitor() {
        throw new VisitorNotFoundException("XsdAttribute shouldn't have visitors");
    }

    public String getDefaultElement() {
        return defaultElement;
    }

    public String getFixed() {
        return fixed;
    }

    public String getType() {
        return type;
    }

    public static XsdElementBase parse(Node node) {
        // TODO Still missing the parsing of contained elements such as SimpleTypes.

        return new XsdAttribute();
    }
}

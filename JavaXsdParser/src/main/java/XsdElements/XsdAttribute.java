package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XsdAttribute extends XsdReferenceElement {

    // TODO public XsdSimpleType simpleType;
    public static final String TAG = "xsd:attribute";
    public static final String DEFAULT_ELEMENT = "defaultElement";
    public static final String FIXED = "fixed";
    public static final String TYPE = "type";

    private String defaultElement;
    private String fixed;
    private String type;

    @Override
    public void setAttributes(NamedNodeMap attributes) {
        super.setAttributes(attributes);

        this.defaultElement = attributes.getNamedItem(DEFAULT_ELEMENT) == null ? null : attributes.getNamedItem(DEFAULT_ELEMENT).getNodeValue();
        this.fixed = attributes.getNamedItem(FIXED) == null ? null : attributes.getNamedItem(FIXED).getNodeValue();
        this.type = attributes.getNamedItem(TYPE) == null ? null : attributes.getNamedItem(TYPE).getNodeValue();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void acceptRefSubstitution(Visitor visitor) {
        System.out.println("REF : " + visitor.getClass() + " com parametro do tipo " + this.getClass());
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

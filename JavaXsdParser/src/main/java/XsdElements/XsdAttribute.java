package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import XsdElements.Visitors.VisitorNotFoundException;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;

public class XsdAttribute extends XsdReferenceElement {

    // TODO public XsdSimpleType simpleType;
    public static final String TAG = "xsd:attribute";
    public static final String DEFAULT_ELEMENT = "defaultElement";
    public static final String FIXED = "fixed";
    public static final String TYPE = "type";

    private String defaultElement;
    private String fixed;
    private String type;

    private XsdAttribute(XsdElementBase parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdAttribute(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        if (elementFieldsMap != null){
            super.setFields(elementFieldsMap);

            this.defaultElement = elementFieldsMap.getOrDefault(DEFAULT_ELEMENT, defaultElement);
            this.fixed = elementFieldsMap.getOrDefault(FIXED, fixed);
            this.type = elementFieldsMap.getOrDefault(TYPE, type);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public Visitor getVisitor() {
        throw new VisitorNotFoundException("XsdAttribute shouldn't have visitors");
    }

    @Override
    List<ReferenceBase> getElements() {
        return null;
    }

    @Override
    public XsdElementBase createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());

        return new XsdAttribute(this.getParent(), placeHolderAttributes);
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

    public static ReferenceBase parse(Node node) {
        // TODO Still missing the parsing of contained elements such as SimpleTypes.
        return ReferenceBase.createFromXsd(new XsdAttribute(convertNodeMap(node.getAttributes())));
    }
}

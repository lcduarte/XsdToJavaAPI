package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import XsdElements.Visitors.VisitorNotFoundException;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;

public class XsdAttribute extends XsdReferenceElement {

    public static final String TAG = "xsd:attribute";

    private AttributeVisitor visitor = new AttributeVisitor();

    public XsdSimpleType simpleType;

    private String defaultElement;
    private String fixed;
    //TODO Encontrar named simpleTypes com o type deste campo.
    private String type;

    private XsdAttribute(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
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
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    @Override
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {

        placeHolderAttributes.putAll(this.getElementFieldsMap());

        XsdAttribute copy = new XsdAttribute(this.getParent(), placeHolderAttributes);

        copy.simpleType = this.simpleType;

        return copy;
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
        return xsdParseSkeleton(node, new XsdAttribute(convertNodeMap(node.getAttributes())));
    }

    class AttributeVisitor extends Visitor{

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAttribute.this;
        }

        @Override
        public void visit(XsdSimpleType element) {
            super.visit(element);

            XsdAttribute.this.simpleType = element;
        }
    }
}

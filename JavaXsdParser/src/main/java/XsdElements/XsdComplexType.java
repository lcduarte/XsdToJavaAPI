package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class XsdComplexType extends XsdElementBase {

    public static final String TAG = "xsd:complexType";
    public static final String NAME = "name";
    public static final String ABSTRACT = "xsd:complexType";
    public static final String MIXED = "xsd:complexType";
    public static final String BLOCK = "xsd:complexType";
    public static final String FINAL = "xsd:complexType";


    private ComplexTypeVisitor visitor = new ComplexTypeVisitor(this);

    private XsdElementBase childElement;

    private String name;
    private String elementAbstract;
    private String mixed;
    private String block;
    private String elementFinal;
    private List<XsdAttribute> attributes = new ArrayList<>();

    @Override
    public void setAttributes(NamedNodeMap attributes) {
        super.setAttributes(attributes);

        this.name = attributes.getNamedItem(NAME) == null ? null : attributes.getNamedItem(NAME).getNodeValue();
        this.elementAbstract = attributes.getNamedItem(ABSTRACT) == null ? null : attributes.getNamedItem(ABSTRACT).getNodeValue();
        this.mixed = attributes.getNamedItem(MIXED) == null ? null : attributes.getNamedItem(MIXED).getNodeValue();
        this.block = attributes.getNamedItem(MIXED) == null ? null : attributes.getNamedItem(MIXED).getNodeValue();
        this.elementFinal = attributes.getNamedItem(MIXED) == null ? null : attributes.getNamedItem(MIXED).getNodeValue();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void acceptRefSubstitution(Visitor visitor) {
        System.out.println("REF : " + visitor.getClass() + " com parametro do tipo " + this.getClass());

        visitor.visitRefChange(this);
    }

    @Override
    public ComplexTypeVisitor getVisitor() {
        return visitor;
    }

    private void setChildElement(XsdMultipleElements element){
        this.childElement = element;
    }

    private void setChildElement(XsdGroup element){
        this.childElement = element;
    }

    private void addAttributes(XsdAttribute attribute) {
        this.attributes.add(attribute);
    }

    void addAttributes(List<XsdAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    public XsdElementBase getChildElement() {
        return childElement;
    }

    public String getName() {
        return name;
    }

    public String getAbstract(){
        return elementAbstract;
    }

    public String getMixed() {
        return mixed;
    }

    public String getBlock() {
        return block;
    }

    public String getFinal() {
        return elementFinal;
    }

    public List<XsdAttribute> getAttributes() {
        return attributes;
    }

    public static XsdElementBase parse(Node node){
        return xsdParseSkeleton(node, new XsdComplexType());
    }

    class ComplexTypeVisitor extends Visitor {

        private final XsdComplexType owner;

        ComplexTypeVisitor(XsdComplexType owner){
            this.owner = owner;
        }

        @Override
        public XsdComplexType getOwner() {
            return owner;
        }

        @Override
        protected XsdReferenceElement getReferenceOwner() {
            return null;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);
            owner.setChildElement(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            owner.setChildElement(element);
        }

        @Override
        public void visit(XsdAttribute attribute) {
            super.visit(attribute);
            owner.addAttributes(attribute);
        }

    }
}

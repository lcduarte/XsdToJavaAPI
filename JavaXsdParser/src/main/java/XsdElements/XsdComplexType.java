package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
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

    private ComplexTypeVisitor visitor = new ComplexTypeVisitor();

    private ReferenceBase childElement;

    private String name;
    private String elementAbstract;
    private String mixed;
    private String block;
    private String elementFinal;
    private List<ReferenceBase> attributes = new ArrayList<>();

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
        this.setParent(visitor.getOwner());
    }

    @Override
    public ComplexTypeVisitor getVisitor() {
        return visitor;
    }

    @Override
    public List<ReferenceBase> getElements() {
        return childElement == null ? null : childElement.getElement().getElements();
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (element.getElement() instanceof  XsdAttributeGroup){
            XsdAttributeGroup attributeGroup = (XsdAttributeGroup) element.getElement();

            this.addAttributes(attributeGroup.getElements());
        }
    }

    private void setChildElement(XsdMultipleElements element){
        this.childElement = ReferenceBase.createFromXsd(element);
    }

    private void setChildElement(XsdGroup element){
        this.childElement = ReferenceBase.createFromXsd(element);
    }

    private void addAttributes(ReferenceBase attribute) {
        this.attributes.add(attribute);
    }

    private void addAttributes(List<ReferenceBase> attributes) {
        this.attributes.addAll(attributes);
    }

    public ReferenceBase getChildElement() {
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

    public List<ReferenceBase> getAttributes() {
        return attributes;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdComplexType());
    }

    class ComplexTypeVisitor extends Visitor {

        @Override
        public XsdElementBase getOwner() {
            return XsdComplexType.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);
            XsdComplexType.this.setChildElement(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdComplexType.this.setChildElement(element);
        }

        @Override
        public void visit(XsdAttribute attribute) {
            super.visit(attribute);
            XsdComplexType.this.addAttributes(ReferenceBase.createFromXsd(attribute));
        }

    }
}

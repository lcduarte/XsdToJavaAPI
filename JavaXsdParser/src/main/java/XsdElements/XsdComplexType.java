package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class XsdComplexType extends XsdElementBase {

    public static final String TAG = "xsd:complexType";

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

        this.name = attributes.getNamedItem("name") == null ? null : attributes.getNamedItem("name").getNodeValue();
        this.elementAbstract = attributes.getNamedItem("abstract") == null ? null : attributes.getNamedItem("abstract").getNodeValue();
        this.mixed = attributes.getNamedItem("mixed") == null ? null : attributes.getNamedItem("mixed").getNodeValue();
        this.block = attributes.getNamedItem("block") == null ? null : attributes.getNamedItem("block").getNodeValue();
        this.elementFinal = attributes.getNamedItem("final") == null ? null : attributes.getNamedItem("final").getNodeValue();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
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

    private void addAttributes(List<XsdAttribute> attributes) {
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

    public static void replaceReferenceElement(XsdElementBase elementBase, XsdReferenceElement referenceElement) {
        if (elementBase instanceof XsdComplexType){
            XsdComplexType complexType = (XsdComplexType) elementBase;

            if (referenceElement instanceof XsdAttributeGroup){
                complexType.addAttributes(((XsdAttributeGroup)referenceElement).getAttributes());
                return;
            }

            XsdElementBase complexChild = complexType.getChildElement();

            if (complexChild instanceof XsdGroup){
                XsdGroup.replaceReferenceElement(complexChild, referenceElement);
            }

            if (complexChild instanceof XsdMultipleElements){
                XsdMultipleElements.replaceReferenceElement(complexChild, referenceElement);
            }
        }
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

package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class XsdComplexType extends XsdElementBase {

    public static final String TAG = "xsd:complexType";
    public static final String NAME = "name";
    public static final String ABSTRACT = "abstract";
    public static final String MIXED = "mixed";
    public static final String BLOCK = "block";
    public static final String FINAL = "final";

    private ComplexTypeVisitor visitor = new ComplexTypeVisitor();

    private ReferenceBase childElement;

    private String name;
    private String elementAbstract;
    private String mixed;
    private String block;
    private String elementFinal;
    private List<ReferenceBase> attributes = new ArrayList<>();

    private XsdComplexType(XsdElementBase parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdComplexType(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        if (elementFieldsMap != null){
            super.setFields(elementFieldsMap);

            this.name = elementFieldsMap.getOrDefault(NAME, name);
            this.elementAbstract = elementFieldsMap.getOrDefault(ABSTRACT, elementAbstract);
            this.mixed = elementFieldsMap.getOrDefault(MIXED, mixed);
            this.block = elementFieldsMap.getOrDefault(BLOCK, block);
            this.elementFinal = elementFieldsMap.getOrDefault(FINAL, elementFinal);
        }
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
    List<ReferenceBase> getElements() {
        return childElement == null ? null : childElement.getElement().getElements();
    }

    @Override
    public XsdElementBase createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdComplexType elementCopy = new XsdComplexType(this.getParent(), placeHolderAttributes);

        elementCopy.childElement = this.childElement;
        elementCopy.addAttributes(this.getAttributes());

        return elementCopy;
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

    ReferenceBase getChildElement() {
        return childElement;
    }

    public XsdElementBase getXsdChildElement() {
        return childElement == null ? null : childElement.getElement();
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

    List<ReferenceBase> getAttributes() {
        return attributes;
    }

    public List<XsdAttribute> getXsdAttributes() {
        return attributes.stream()
                        .filter(attribute -> attribute instanceof ConcreteElement)
                        .filter(attribute -> attribute.getElement() instanceof  XsdAttribute)
                        .map(attribute -> (XsdAttribute)attribute.getElement())
                        .collect(Collectors.toList());
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdComplexType(convertNodeMap(node.getAttributes())));
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

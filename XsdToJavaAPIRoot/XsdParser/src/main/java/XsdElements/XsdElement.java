package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.Visitors.Visitor;
import XsdParser.XsdParser;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;

public class XsdElement extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:element";
    public static final String XS_TAG = "xs:element";

    private Visitor visitor = new ElementVisitor();

    private ReferenceBase complexType;

    private ReferenceBase type;
    private String substitutionGroup;
    private String defaultObj;
    private String fixed;
    private String form;
    private String nillable;
    private String abstractObj;
    private String block;
    private String finalObj;

    private XsdElement(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    public XsdElement(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    private XsdElement(XsdAbstractElement parent) {
        super(parent);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            String type = elementFieldsMap.get(TYPE);

            if (type != null){
                if (XsdParser.getXsdTypesToJava().containsKey(type)){
                    HashMap<String, String> attributes = new HashMap<>();
                    attributes.put(NAME, type);
                    this.type = ReferenceBase.createFromXsd(new XsdComplexType(this, attributes));
                } else {
                    XsdElement placeHolder = new XsdElement(this);
                    this.type = new UnsolvedReference(type, placeHolder);
                    XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.type);
                }
            }

            this.substitutionGroup = elementFieldsMap.getOrDefault(SUBSTITUTION_GROUP, substitutionGroup);
            this.defaultObj = elementFieldsMap.getOrDefault(DEFAULT, defaultObj);
            this.fixed = elementFieldsMap.getOrDefault(FIXED, fixed);
            this.form = elementFieldsMap.getOrDefault(FORM, form);
            this.nillable = elementFieldsMap.getOrDefault(NILLABLE, nillable);
            this.abstractObj = elementFieldsMap.getOrDefault(ABSTRACT, abstractObj);
            this.block = elementFieldsMap.getOrDefault(BLOCK, block);
            this.finalObj = elementFieldsMap.getOrDefault(FINAL, finalObj);
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
    public XsdElement clone(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        return new XsdElement(this.getParent(), placeHolderAttributes);
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (this.type != null && this.type instanceof UnsolvedReference && ((UnsolvedReference) this.type).getRef().equals(element.getName())){
            this.type = element;
            element.getElement().setParent(this);
        }
    }

    private void setComplexType(XsdComplexType complexType) {
        this.complexType = ReferenceBase.createFromXsd(complexType);
    }

    public XsdComplexType getXsdComplexType() {
        return complexType == null ? type == null ? null : (XsdComplexType) type.getElement() : (XsdComplexType) complexType.getElement();
    }

    public XsdAbstractElement getXsdType(){
        if (type != null && type instanceof ConcreteElement){
            return type.getElement();
        }

        return null;
    }

    public String getFinal() {
        return finalObj;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdElement(convertNodeMap(node.getAttributes())));
    }

    class ElementVisitor extends Visitor{

        @Override
        public XsdAbstractElement getOwner() {
            return XsdElement.this;
        }

        @Override
        public void visit(XsdComplexType element) {
            super.visit(element);
            XsdElement.this.setComplexType(element);
        }
    }
}

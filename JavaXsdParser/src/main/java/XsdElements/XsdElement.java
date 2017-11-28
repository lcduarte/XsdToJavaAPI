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

    public static final String TAG = "xsd:element";
    public static final String TYPE = "type";
    public static final String SUBSTITUTION_GROUP = "substitutionGroup";
    public static final String DEFAULT = "default";
    public static final String FIXED = "fixed";
    public static final String FORM = "form";
    public static final String NILLABLE = "nillable";
    public static final String ABSTRACT = "abstract";
    public static final String BLOCK = "block";
    public static final String FINAL = "final";

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

    private XsdElement(XsdElementBase parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdElement(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    private XsdElement(XsdElementBase parent) {
        super(parent);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        if (elementFieldsMap != null){
            super.setFields(elementFieldsMap);

            if (elementFieldsMap.containsKey(TYPE)){
                XsdElement placeHolder = new XsdElement(this);
                this.type = new UnsolvedReference(elementFieldsMap.get(TYPE), placeHolder);
                XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) type);
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
    public List<ReferenceBase> getElements() {
        return null;
    }

    @Override
    public XsdElementBase createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        return new XsdElement(this.getParent(), placeHolderAttributes);
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (this.type != null && this.type instanceof UnsolvedReference && ((UnsolvedReference) this.type).getRef().equals(element.getName())){
            this.type = element;
        }
    }

    private void setComplexType(XsdComplexType complexType) {
        this.complexType = ReferenceBase.createFromXsd(complexType);
    }

    public XsdComplexType getComplexType() {
        return complexType == null ? null : (XsdComplexType) complexType.getElement();
    }

    public ReferenceBase getType(){
        return type;
    }

    public String getSubstitutionGroup() {
        return substitutionGroup;
    }

    public String getDefault() {
        return defaultObj;
    }

    public String getFixed() {
        return fixed;
    }

    public String getForm() {
        return form;
    }

    public String getNillable() {
        return nillable;
    }

    public String getAbstract() {
        return abstractObj;
    }

    public String getBlock() {
        return block;
    }

    public String getFinal() {
        return finalObj;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdElement(convertNodeMap(node.getAttributes())));
    }

    class ElementVisitor extends Visitor{

        @Override
        public XsdElementBase getOwner() {
            return XsdElement.this;
        }

        @Override
        public void visit(XsdComplexType element) {
            super.visit(element);
            XsdElement.this.setComplexType(element);
        }
    }
}

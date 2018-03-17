package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XsdElement extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:element";
    public static final String XS_TAG = "xs:element";

    private XsdElementVisitor xsdElementVisitor = new ElementXsdElementVisitor();

    private ReferenceBase complexType;

    private ReferenceBase type;
    private String substitutionGroup;
    private String defaultObj;
    private String fixed;
    private String form;
    private boolean nillable;
    private boolean abstractObj;
    private String block;
    private String finalObj;
    private Integer minOccurs;
    private String maxOccurs;

    protected XsdElement(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    public XsdElement(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            String typeString = elementFieldsMap.get(TYPE_TAG);

            if (typeString != null){
                if (XsdParser.getXsdTypesToJava().containsKey(typeString)){
                    HashMap<String, String> attributes = new HashMap<>();
                    attributes.put(NAME_TAG, typeString);
                    this.type = ReferenceBase.createFromXsd(new XsdComplexType(this, attributes));
                } else {
                    XsdElement placeHolder = new XsdElement(this, null);
                    this.type = new UnsolvedReference(typeString, placeHolder);
                    XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.type);
                }
            }

            this.substitutionGroup = elementFieldsMap.getOrDefault(SUBSTITUTION_GROUP_TAG, substitutionGroup);
            this.defaultObj = elementFieldsMap.getOrDefault(DEFAULT_TAG, defaultObj);
            this.fixed = elementFieldsMap.getOrDefault(FIXED_TAG, fixed);
            this.form = elementFieldsMap.getOrDefault(FORM_TAG, form);
            this.nillable = Boolean.parseBoolean(elementFieldsMap.getOrDefault(NILLABLE_TAG, "false"));
            this.abstractObj = Boolean.parseBoolean(elementFieldsMap.getOrDefault(ABSTRACT_TAG, "false"));
            this.block = elementFieldsMap.getOrDefault(BLOCK_TAG, block);
            this.finalObj = elementFieldsMap.getOrDefault(FINAL_TAG, finalObj);
            this.minOccurs = Integer.parseInt(elementFieldsMap.getOrDefault(MIN_OCCURS_TAG, "1"));
            this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS_TAG, "1");
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return xsdElementVisitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }

    @Override
    public XsdElement clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());

        placeHolderAttributes.remove(TYPE_TAG);

        XsdElement elementCopy = new XsdElement(this.getParent(), placeHolderAttributes);

        elementCopy.type = this.type;

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (this.type != null && this.type instanceof UnsolvedReference && element.getElement() instanceof XsdComplexType && ((UnsolvedReference) this.type).getRef().equals(element.getName())){
            this.type = element;
            element.getElement().setParent(this);
        }
    }

    public XsdComplexType getXsdComplexType() {
        return complexType == null ? getXsdType() : (XsdComplexType) complexType.getElement();
    }

    private XsdComplexType getXsdType(){
        if (type != null && type instanceof ConcreteElement){
            return (XsdComplexType) type.getElement();
        }

        return null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdElement(convertNodeMap(node.getAttributes())));
    }

    public String getFinal() {
        return finalObj;
    }

    @SuppressWarnings("unused")
    public boolean isNillable() {
        return nillable;
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }

    @SuppressWarnings("unused")
    public boolean isAbstractObj() {
        return abstractObj;
    }

    class ElementXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdElement.this;
        }

        @Override
        public void visit(XsdComplexType element) {
            super.visit(element);
            XsdElement.this.complexType = ReferenceBase.createFromXsd(element);
        }
    }
}

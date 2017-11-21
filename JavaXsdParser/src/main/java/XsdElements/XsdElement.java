package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Optional;

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

    private Visitor visitor = new ElementVisitor(this);

    private XsdComplexType complexType;

    private String type;
    private String substitutionGroup;
    private String defaultObj;
    private String fixed;
    private String form;
    private String nillable;
    private String abstractObj;
    private String block;
    private String finalObj;

    public void setAttributes(NamedNodeMap attributes){
        super.setAttributes(attributes);

        this.type = attributes.getNamedItem(TYPE) == null ? null : attributes.getNamedItem("type").getNodeValue();
        this.substitutionGroup = attributes.getNamedItem(SUBSTITUTION_GROUP) == null ? null : attributes.getNamedItem(SUBSTITUTION_GROUP).getNodeValue();
        this.defaultObj = attributes.getNamedItem(DEFAULT) == null ? null : attributes.getNamedItem(DEFAULT).getNodeValue();
        this.fixed = attributes.getNamedItem(FIXED) == null ? null : attributes.getNamedItem(FIXED).getNodeValue();
        this.form = attributes.getNamedItem(FORM) == null ? null : attributes.getNamedItem(FORM).getNodeValue();
        this.nillable = attributes.getNamedItem(NILLABLE) == null ? null : attributes.getNamedItem(NILLABLE).getNodeValue();
        this.abstractObj = attributes.getNamedItem(ABSTRACT) == null ? null : attributes.getNamedItem(ABSTRACT).getNodeValue();
        this.block = attributes.getNamedItem(BLOCK) == null ? null : attributes.getNamedItem(BLOCK).getNodeValue();
        this.finalObj = attributes.getNamedItem(FINAL) == null ? null : attributes.getNamedItem(FINAL).getNodeValue();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void acceptRefSubstitution(Visitor visitor) {

    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    private void setComplexType(XsdComplexType complexType) {
        this.complexType = complexType;
    }

    public XsdComplexType getComplexType() {
        return complexType;
    }

    public String getType(){
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

    public static XsdElementBase parse(Node node){
        return xsdParseSkeleton(node, new XsdElement());
    }

    class ElementVisitor extends Visitor{

        XsdElement owner;

        ElementVisitor(XsdElement owner){
            this.owner = owner;
        }

        @Override
        public XsdElement getOwner() {
            return owner;
        }

        @Override
        public XsdReferenceElement getReferenceOwner() {
            return owner;
        }

        @Override
        public void visit(XsdComplexType element) {
            super.visit(element);
            owner.setComplexType(element);
        }
    }
}

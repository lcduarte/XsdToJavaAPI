package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XsdElement extends XsdReferenceElement {

    public static final String TAG = "xsd:element";

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

        this.type = attributes.getNamedItem("type") == null ? null : attributes.getNamedItem("type").getNodeValue();
        this.substitutionGroup = attributes.getNamedItem("substitutionGroup") == null ? null : attributes.getNamedItem("substitutionGroup").getNodeValue();
        this.defaultObj = attributes.getNamedItem("default") == null ? null : attributes.getNamedItem("default").getNodeValue();
        this.fixed = attributes.getNamedItem("fixed") == null ? null : attributes.getNamedItem("fixed").getNodeValue();
        this.form = attributes.getNamedItem("form") == null ? null : attributes.getNamedItem("form").getNodeValue();
        this.nillable = attributes.getNamedItem("nillable") == null ? null : attributes.getNamedItem("nillable").getNodeValue();
        this.abstractObj = attributes.getNamedItem("abstract") == null ? null : attributes.getNamedItem("abstract").getNodeValue();
        this.block = attributes.getNamedItem("block") == null ? null : attributes.getNamedItem("block").getNodeValue();
        this.finalObj = attributes.getNamedItem("final") == null ? null : attributes.getNamedItem("final").getNodeValue();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
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
        public void visit(XsdComplexType element) {
            super.visit(element);
            owner.setComplexType(element);
        }
    }
}

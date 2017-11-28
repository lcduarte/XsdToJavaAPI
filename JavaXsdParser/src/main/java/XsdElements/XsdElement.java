package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.Visitors.Visitor;
import XsdParser.XsdParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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

    public void setAttributes(NamedNodeMap attributes){
        super.setAttributes(attributes);

        if (attributes.getNamedItem(TYPE) != null){
            XsdElement placeHolder = new XsdElement();
            placeHolder.setParent(this);

            this.type = new UnsolvedReference(attributes.getNamedItem(TYPE).getNodeValue(), placeHolder);
            XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) type);
        }

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
        return xsdParseSkeleton(node, new XsdElement());
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

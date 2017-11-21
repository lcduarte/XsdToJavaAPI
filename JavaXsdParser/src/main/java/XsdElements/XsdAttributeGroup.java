package XsdElements;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class XsdAttributeGroup extends XsdReferenceElement {

    public static final String TAG = "xsd:attributeGroup";
    private final AttributeGroupVisitor visitor = new AttributeGroupVisitor(this);

    private List<XsdAttribute> attributes = new ArrayList<>();

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
    public Visitor getVisitor() {
        return visitor;
    }

    private void addAttributes(XsdAttribute attribute) {
        this.attributes.add(attribute);
    }

    private void addAttributes(List<XsdAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    List<XsdAttribute> getAttributes() {
        return attributes;
    }

    public static XsdElementBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAttributeGroup());
    }

    class AttributeGroupVisitor extends Visitor {
        private final XsdAttributeGroup owner;

        AttributeGroupVisitor(XsdAttributeGroup owner) {
            this.owner = owner;
        }

        @Override
        public XsdAttributeGroup getOwner() {
            return owner;
        }

        @Override
        protected XsdReferenceElement getReferenceOwner() {
            return owner;
        }

        @Override
        public void visit(XsdAttribute element) {
            super.visit(element);
            owner.addAttributes(element);
        }

        @Override
        public void visitRefChange(XsdAttributeGroup element) {
            super.visitRefChange(element);

            element.addAttributes(owner.getAttributes());
        }

        @Override
        public void visitRefChange(XsdComplexType element) {
            super.visitRefChange(element);

            element.addAttributes(owner.getAttributes());
        }
    }
}

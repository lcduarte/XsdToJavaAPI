package XsdElements;

import XsdElements.Visitors.RefVisitor;
import XsdElements.Visitors.Visitor;
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
    public void acceptRefSubstitution(RefVisitor visitor) {
        //System.out.println("REF : " + visitor.getClass() + " with parameter type " + this.getClass());

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

    class AttributeGroupVisitor extends RefVisitor {
        private final XsdAttributeGroup owner;

        AttributeGroupVisitor(XsdAttributeGroup owner) {
            this.owner = owner;
        }

        @Override
        public XsdAttributeGroup getOwner() {
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

            owner.addAttributes(element.getAttributes());
        }
    }
}

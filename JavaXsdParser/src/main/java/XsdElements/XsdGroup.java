package XsdElements;

import org.w3c.dom.Node;

public class XsdGroup extends XsdReferenceElement {

    public static final String TAG = "xsd:group";

    private GroupVisitor visitor = new GroupVisitor(this);

    private XsdMultipleElements childElement;

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
    public GroupVisitor getVisitor() {
        return visitor;
    }

    private void setChildElement(XsdMultipleElements childElement) {
        this.childElement = childElement;
    }

    public XsdMultipleElements getChildElement() {
        return childElement;
    }

    public static XsdElementBase parse(Node node){
        return xsdParseSkeleton(node, new XsdGroup());
    }

    class GroupVisitor extends Visitor {

        private final XsdGroup owner;

        GroupVisitor(XsdGroup owner){
            this.owner = owner;
        }

        @Override
        public XsdGroup getOwner() {
            return owner;
        }

        @Override
        protected XsdReferenceElement getReferenceOwner() {
            return owner;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);

            owner.setChildElement(element);
        }
    }
}

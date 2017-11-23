package XsdElements;

import XsdElements.Visitors.RefVisitor;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

public class XsdAll extends XsdMultipleElements {

    public static final String TAG = "xsd:all";
    private final AllVisitor visitor = new AllVisitor(this);

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    public static XsdElementBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAll());
    }

    class AllVisitor extends RefVisitor {

        XsdAll owner;

        AllVisitor(XsdAll owner){
            this.owner = owner;
        }

        @Override
        public XsdAll getOwner() {
            return owner;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            owner.addElement(element);
        }

        @Override
        public void visitRefChange(XsdGroup element) {
            super.visitRefChange(element);

            baseRefChange(owner, element);
        }

        @Override
        public void visitRefChange(XsdElement element) {
            super.visitRefChange(element);

            baseRefChange(owner, element);
        }
    }
}

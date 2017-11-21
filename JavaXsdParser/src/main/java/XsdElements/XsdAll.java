package XsdElements;

import org.w3c.dom.Node;

public class XsdAll extends XsdMultipleElements {

    public static final String TAG = "xsd:all";
    private final AllVisitor visitor = new AllVisitor(this);

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

    public static XsdElementBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAll());
    }

    class AllVisitor extends Visitor{

        XsdAll owner;

        AllVisitor(XsdAll owner){
            this.owner = owner;
        }

        @Override
        public XsdAll getOwner() {
            return owner;
        }

        @Override
        protected XsdReferenceElement getReferenceOwner() {
            return null;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            owner.addElement(element);
        }
    }
}

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
    public Visitor getVisitor() {
        return visitor;
    }

    private void addElement(XsdElement element){
        super.addElement(element);
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
        public void visit(XsdElement element) {
            super.visit(element);
            owner.addElement(element);
        }
    }
}

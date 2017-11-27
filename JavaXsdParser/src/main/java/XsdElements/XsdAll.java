package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

public class XsdAll extends XsdMultipleElements {

    public static final String TAG = "xsd:all";
    private final AllVisitor visitor = new AllVisitor();

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAll());
    }

    class AllVisitor extends Visitor {

        @Override
        public XsdElementBase getOwner() {
            return XsdAll.this;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            XsdAll.this.addElement(ReferenceBase.createFromXsd(element));
        }
    }
}

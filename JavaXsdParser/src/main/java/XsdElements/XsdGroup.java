package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.List;

public class XsdGroup extends XsdReferenceElement {

    public static final String TAG = "xsd:group";

    private GroupVisitor visitor = new GroupVisitor();

    private XsdMultipleElements childElement;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public GroupVisitor getVisitor() {
        return visitor;
    }

    @Override
    public List<ReferenceBase> getElements() {
        return childElement.getElements();
    }

    private void setChildElement(XsdMultipleElements childElement) {
        childElement.getElements().forEach(childElementObj -> childElementObj.getElement().setParent(this));
        this.childElement = childElement;
    }

    XsdMultipleElements getChildElement() {
        return childElement;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdGroup());
    }

    class GroupVisitor extends Visitor {

        @Override
        public XsdElementBase getOwner() {
            return XsdGroup.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);

            XsdGroup.this.setChildElement(element);
        }
    }
}

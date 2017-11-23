package XsdElements;

import XsdElements.Visitors.RefVisitor;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

public class XsdChoice extends XsdMultipleElements{

    public static final String TAG = "xsd:choice";

    private ChoiceVisitor visitor = new ChoiceVisitor(this);;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ChoiceVisitor getVisitor() {
        return visitor;
    }

    private void addElement(XsdGroup groupElement){
        XsdMultipleElements groupChild = groupElement.getChildElement();

        if (groupChild != null){
            super.addGroup(groupElement.getName(), groupElement.getChildElement().getElements());
        } else {
            addUnsolvedGroup(groupElement);
        }
    }

    private void addElement(XsdChoice choiceElement){
        super.addElements(choiceElement.getElements());
    }

    private void addElement(XsdSequence sequenceElement){
        super.addElements(sequenceElement.getElements());
    }

    public static XsdElementBase parse(Node node){
        return xsdParseSkeleton(node, new XsdChoice());
    }

    class ChoiceVisitor extends RefVisitor {

        private final XsdChoice owner;

        ChoiceVisitor(XsdChoice owner){
            this.owner = owner;
        }

        @Override
        public XsdChoice getOwner() {
            return owner;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            owner.addElement(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            owner.addElement(element);
        }

        @Override
        public void visit(XsdChoice element) {
            super.visit(element);
            owner.addElement(element);
        }

        @Override
        public void visit(XsdSequence element) {
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

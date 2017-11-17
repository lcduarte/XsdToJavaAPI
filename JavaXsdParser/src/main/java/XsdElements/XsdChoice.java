package XsdElements;

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

    private void addElement(XsdElement element){
        super.addElement(element);
    }

    private void addElement(XsdGroup groupElement){
        super.addElement(groupElement);
    }

    private void addElement(XsdChoice choiceElement){
        super.addElement(choiceElement);
    }

    private void addElement(XsdSequence sequenceElement){
        super.addElement(sequenceElement);
    }

    public static XsdElementBase parse(Node node){
        return xsdParseSkeleton(node, new XsdChoice());
    }

    class ChoiceVisitor extends Visitor{

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
    }
}

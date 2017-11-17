package XsdElements;

import org.w3c.dom.Node;

public class XsdSequence extends XsdMultipleElements{

    public static final String TAG = "xsd:sequence";

    private SequenceVisitor visitor = new SequenceVisitor(this);

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SequenceVisitor getVisitor() {
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
        return xsdParseSkeleton(node, new XsdSequence());
    }

    class SequenceVisitor extends Visitor{

        private final XsdSequence owner;

        SequenceVisitor(XsdSequence owner){
            this.owner = owner;
        }

        @Override
        public XsdSequence getOwner() {
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

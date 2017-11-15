package XsdElements;

import org.w3c.dom.Node;

public class XsdSequence extends XsdMultipleElements{

    public XsdSequence(Node node) {
        super(node);
    }

    public void addElement(XsdElement element){
        super.addElement(element);
    }

    public void addElement(XsdGroup groupElement){
        super.addElement(groupElement);
    }

    public void addElement(XsdChoice choiceElement){
        super.addElement(choiceElement);
    }

    public void addElement(XsdSequence sequenceElement){
        super.addElement(sequenceElement);
    }

}

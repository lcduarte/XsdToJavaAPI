package XsdElements;

import XsdParser.XsdParser;

import java.util.Optional;

public abstract class Visitor {

    void visit(XsdAll element) {
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdAttribute element) {
        visit((XsdReferenceElement) element);
    }

    void visit(XsdAttributeGroup element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdChoice element) {
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdComplexType element) {

    }

    public void visit(XsdElement element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdGroup element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdSequence element){
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdMultipleElements element){

    }

    private void visit(XsdReferenceElement element){
        if (element.getRef() != null){
            element.setParent(getOwner());
            XsdParser.getInstance().addUnsolvedReference(element);
        }
    }

    protected abstract XsdElementBase getOwner();

}

package XsdElements;

import XsdParser.XsdParser;

public abstract class Visitor {

    public void visit(XsdAll element) {
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdAttribute element) {
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdAttributeGroup element){
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

    public void visit(XsdReferenceElement element){
        if (element.getRef() != null){
            element.setParent(getOwner());
            XsdParser.getInstance().addUnsolvedReference(element);
        }
    }

    protected abstract XsdElementBase getOwner();
}

package XsdElements.Visitors;

import XsdElements.*;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
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

    private void visit(XsdReferenceElement element){
        ReferenceBase referenceBase = ReferenceBase.createFromXsd(element);

        if (referenceBase instanceof UnsolvedReference){
            XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) referenceBase);
        }
    }

    public abstract XsdElementBase getOwner();
}

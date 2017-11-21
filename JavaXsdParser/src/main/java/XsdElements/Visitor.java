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

    public void visitRefChange(XsdAttributeGroup element){

    }

    public void visitRefChange(XsdComplexType element) {

    }

    void visitRefChange(XsdGroup element){
        baseRefChange(element.getChildElement(), getReferenceOwner());
    }

    void visitRefChange(XsdAll element) {
        baseRefChange(element, getReferenceOwner());
    }

    void visitRefChange(XsdChoice element) {
        baseRefChange(element, getReferenceOwner());
    }

    void visitRefChange(XsdSequence element) {
        baseRefChange(element, getReferenceOwner());
    }

    private void baseRefChange(XsdMultipleElements owner, XsdReferenceElement referenceElement){
        if (referenceElement instanceof XsdGroup){
            baseRefChangeForGroups(owner, (XsdGroup) referenceElement);
        }

        if (referenceElement instanceof XsdElement){
            baseRefChangeForElements(owner, (XsdElement) referenceElement);
        }
    }

    private void baseRefChangeForGroups(XsdMultipleElements owner, XsdGroup group) {
        if (owner.getUnresolvedGroups().stream()
                .filter(referenceElementObj -> referenceElementObj.getRef() != null &&
                        referenceElementObj.getRef().equals(group.getName()))
                .count() == 1){
            owner.addGroup(group.getName(), group.getChildElement().getElements());
            owner.removeUnsolvedGroup(group);
        }
    }

    private void baseRefChangeForElements(XsdMultipleElements owner, XsdElement element){
        Optional<XsdElement> oldElement = owner.getElements().stream()
                .filter(referenceElementObj -> referenceElementObj.getRef() != null &&
                        referenceElementObj.getRef().equals(element.getName()))
                .findFirst();

        if (oldElement.isPresent()){
            owner.replaceElement(owner.getElements().indexOf(oldElement.get()), element);
        } else {
            System.err.println("Não devia estar aqui");
        }
    }

    protected abstract XsdElementBase getOwner();

    protected abstract XsdReferenceElement getReferenceOwner();

}

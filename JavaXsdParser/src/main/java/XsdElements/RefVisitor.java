package XsdElements;

public abstract class RefVisitor extends Visitor {

    public void visitRefChange(XsdAttribute element) {

    }

    public void visitRefChange(XsdAttributeGroup element){

    }

    public void visitRefChange(XsdElement element) {

    }

    public void visitRefChange(XsdGroup element){

    }

    void baseRefChange(XsdMultipleElements owner, XsdGroup group) {
        if (owner.getUnresolvedGroups().stream()
                .filter(referenceElementObj -> referenceElementObj.getRef() != null &&
                        referenceElementObj.getRef().equals(group.getName()))
                .count() == 1){
            owner.addGroup(group.getName(), group.getChildElement().getElements());
            owner.removeUnsolvedGroup(group);
        }
    }

    void baseRefChange(XsdMultipleElements owner, XsdElement element){
        owner.getElements()
                .stream()
                .filter(referenceElementObj -> referenceElementObj.getRef() != null &&
                        referenceElementObj.getRef().equals(element.getName()))
                .findFirst()
                .ifPresent(oldElement -> owner.replaceElement(owner.getElements().indexOf(oldElement), element));
    }

}

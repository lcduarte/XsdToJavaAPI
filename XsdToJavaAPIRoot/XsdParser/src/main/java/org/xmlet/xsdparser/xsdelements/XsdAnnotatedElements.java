package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.List;
import java.util.Map;

public abstract class XsdAnnotatedElements extends XsdIdentifierElements {

    private XsdAnnotation annotation;

    protected XsdAnnotatedElements(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    protected XsdAnnotatedElements(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    protected void setAnnotation(XsdAnnotation annotation){
        this.annotation = annotation;
    }

    @SuppressWarnings("unused")
    public XsdAnnotation getAnnotation() {
        return annotation;
    }

    protected class AnnotatedXsdElementVisitor extends XsdElementVisitor {
        @Override
        public XsdAbstractElement getOwner() {
            return XsdAnnotatedElements.this;
        }

        @Override
        public void visit(XsdAnnotation element) {
            super.visit(element);

            setAnnotation(element);
        }
    }

    void replaceUnsolvedAttributes(ConcreteElement element, List<ReferenceBase> attributeGroups, List<ReferenceBase> attributes){
        if (element.getElement() instanceof XsdAttributeGroup){
            attributeGroups.stream()
                    .filter(attributeGroup -> attributeGroup instanceof UnsolvedReference && ((UnsolvedReference) attributeGroup).getRef().equals(element.getName()))
                    .findFirst().ifPresent(referenceBase -> {
                attributeGroups.remove(referenceBase);
                attributeGroups.add(element);
                attributes.addAll(element.getElement().getElements());

                element.getElement().setParent(this);
            });
        }

        if (element.getElement() instanceof XsdAttribute ){
            attributes.stream()
                    .filter(attribute -> attribute instanceof UnsolvedReference && ((UnsolvedReference) attribute).getRef().equals(element.getName()))
                    .findFirst().ifPresent(referenceBase -> {
                attributes.remove(referenceBase);
                attributes.add(element);
                element.getElement().setParent(this);
            });
        }
    }
}

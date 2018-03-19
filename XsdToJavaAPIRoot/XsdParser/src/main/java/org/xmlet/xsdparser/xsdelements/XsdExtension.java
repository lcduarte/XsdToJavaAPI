package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XsdExtension extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:extension";
    public static final String XS_TAG = "xs:extension";

    private XsdElementVisitor xsdElementVisitor = new ExtensionXsdElementVisitor();

    private ReferenceBase childElement;
    private List<ReferenceBase> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

    private ReferenceBase base;

    private XsdExtension(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            String baseValue = elementFieldsMap.getOrDefault(BASE_TAG, null);

            if (baseValue != null){
                XsdElement placeHolder = new XsdElement(null);
                placeHolder.setParent(this);
                this.base = new UnsolvedReference(baseValue, placeHolder);
                XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.base);
            }
        }
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (this.base != null && this.base instanceof UnsolvedReference && element.getElement() instanceof XsdElement && ((UnsolvedReference) this.base).getRef().equals(element.getName())){
            this.base = element;
        }

        replaceUnsolvedAttributes(element, attributeGroups, attributes);
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return xsdElementVisitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return childElement == null ? Collections.emptyList() : childElement.getElement().getElements();
    }

    public XsdElement getBase() {
        return base instanceof ConcreteElement ? (XsdElement) base.getElement() : null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdExtension(convertNodeMap(node.getAttributes())));
    }

    public Stream<XsdAttribute> getXsdAttributes() {
        return attributes.stream()
                .filter(attribute -> attribute instanceof ConcreteElement)
                .filter(attribute -> attribute.getElement() instanceof  XsdAttribute)
                .map(attribute -> (XsdAttribute)attribute.getElement());
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return attributeGroups.stream()
                .filter(attributeGroup -> attributeGroup instanceof ConcreteElement)
                .map(attributeGroup -> (XsdAttributeGroup) attributeGroup.getElement());
    }

    class ExtensionXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdExtension.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);
            XsdExtension.this.childElement = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdExtension.this.childElement = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdAttribute attribute) {
            super.visit(attribute);
            XsdExtension.this.attributes.add(ReferenceBase.createFromXsd(attribute));
        }

        @Override
        public void visit(XsdAttributeGroup attributeGroup) {
            super.visit(attributeGroup);

            XsdExtension.this.attributeGroups.add(ReferenceBase.createFromXsd(attributeGroup));
        }
    }

}

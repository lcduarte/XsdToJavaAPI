package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XsdAnnotation extends XsdIdentifierElements {

    public static final String XSD_TAG = "xsd:annotation";
    public static final String XS_TAG = "xs:annotation";

    private XsdElementVisitor xsdElementVisitor = new AnnotationXsdElementVisitor();

    private List<XsdAppInfo> appInfoList = new ArrayList<>();
    private List<XsdDocumentation> documentations = new ArrayList<>();

    private XsdAnnotation(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
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
        return Collections.emptyList();
    }

    public List<XsdAppInfo> getAppInfoList() {
        return appInfoList;
    }

    public List<XsdDocumentation> getDocumentations() {
        return documentations;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdAnnotation(convertNodeMap(node.getAttributes())));
    }

    class AnnotationXsdElementVisitor implements XsdElementVisitor {

        public XsdAbstractElement getOwner() {
            return XsdAnnotation.this;
        }

        public void visit(XsdAppInfo element) {
            XsdElementVisitor.super.visit(element);

            XsdAnnotation.this.appInfoList.add(element);
        }

        public void visit(XsdDocumentation element) {
            XsdElementVisitor.super.visit(element);

            XsdAnnotation.this.documentations.add(element);
        }
    }
}

package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.VisitorNotFoundException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XsdDocumentation extends XsdAbstractElement {

    public static final String XSD_TAG = "xsd:documentation";
    public static final String XS_TAG = "xs:documentation";

    private String source;
    private String xmlLang;
    private String content;

    private XsdDocumentation(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.source = elementFieldsMap.getOrDefault(SOURCE_TAG, source);
            this.xmlLang = elementFieldsMap.getOrDefault(XML_LANG_TAG, xmlLang);
        }
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        throw new VisitorNotFoundException("Documentation can't have children.");
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    public String getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }

    public static ReferenceBase parse(Node node){
        XsdDocumentation documentation = new XsdDocumentation(convertNodeMap(node.getAttributes()));

        documentation.content = xsdRawContentParse(node);

        return ReferenceBase.createFromXsd(documentation);
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }
}

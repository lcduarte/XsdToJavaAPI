package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdWhiteSpace extends XsdAbstractRestrictionChild implements StringValue {

    public static final String XSD_TAG = "xsd:whiteSpace";
    public static final String XS_TAG = "xs:whiteSpace";

    private boolean fixed;
    private String value;

    private XsdWhiteSpace(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            fixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(FIXED_TAG, "false"));
            value = elementFieldsMap.getOrDefault(VALUE_TAG, value);
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdWhiteSpace(convertNodeMap(node.getAttributes())));
    }

    public String getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}

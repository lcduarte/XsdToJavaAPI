package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdMinExclusive extends XsdAbstractRestrictionChild implements IntValue {

    public static final String XSD_TAG = "xsd:minExclusive";
    public static final String XS_TAG = "xs:minExclusive";

    private boolean fixed;
    private int value;

    private XsdMinExclusive(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            fixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(FIXED_TAG, "false"));
            value = Integer.parseInt(elementFieldsMap.getOrDefault(VALUE_TAG, "0"));
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdMinExclusive(convertNodeMap(node.getAttributes())));
    }

    public int getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}

package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdStringRestrictions extends XsdAnnotatedElements{

    private XsdAnnotatedElementsVisitor visitor = new XsdAnnotatedElementsVisitor(this);
    private String value;

    XsdStringRestrictions(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public XsdAnnotatedElementsVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        value = elementFieldsMap.getOrDefault(VALUE_TAG, value);
    }

    public static boolean hasDifferentValue(XsdStringRestrictions o1, XsdStringRestrictions o2) {
        if (o1 == null && o2 == null) {
            return false;
        }

        String o1Value = null;
        String o2Value;

        if (o1 != null) {
            o1Value = o1.getValue();
        }

        if (o2 != null) {
            o2Value = o2.getValue();
            return o2Value.equals(o1Value);
        }

        return false;
    }

    public String getValue() {
        return value;
    }
}

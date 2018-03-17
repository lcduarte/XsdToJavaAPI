package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class XsdAbstractRestrictionChild extends XsdAnnotatedElements {

    private XsdElementVisitor xsdElementVisitor = new AbstractRestrictionChildXsdElementVisitor();

    public XsdAbstractRestrictionChild(){
        super(Collections.emptyMap());
    }

    public XsdAbstractRestrictionChild(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return xsdElementVisitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }

    class AbstractRestrictionChildXsdElementVisitor extends XsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAbstractRestrictionChild.this;
        }

        @Override
        public void visit(XsdAnnotation element) {
            super.visit(element);

            setAnnotation(element);
        }
    }

    public static boolean hasDifferentValue(IntValue o1, IntValue o2) {
        if (o1 == null && o2 == null){
            return false;
        }

        int o1Value = Integer.MAX_VALUE;
        int o2Value;

        if (o1 != null){
            o1Value = o1.getValue();
        }

        if (o2 != null){
            o2Value = o2.getValue();
            return o2Value == o1Value;
        }

        return true;
    }

    public static boolean hasDifferentValue(StringValue o1, StringValue o2) {
        if (o1 == null && o2 == null){
            return false;
        }

        String o1Value = null;
        String o2Value;

        if (o1 != null){
            o1Value = o1.getValue();
        }

        if (o2 != null){
            o2Value = o2.getValue();
            return o2Value.equals(o1Value);
        }

        return true;
    }
}

interface StringValue{
    String getValue();
}

interface IntValue{
    int getValue();
}


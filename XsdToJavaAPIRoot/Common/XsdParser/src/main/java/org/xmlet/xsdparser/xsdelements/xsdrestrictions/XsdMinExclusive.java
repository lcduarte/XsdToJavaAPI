package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * States the minimum numeric value that a given type might take, excluding the respective value. The value is defined
 * as an Integer.
 * e.g.
 *  * If the type has a xsd:minExclusive value="0" it means that the minimum value it can take is any number bigger than 0.
 */
public class XsdMinExclusive extends XsdIntegerRestrictions {

    public static final String XSD_TAG = "xsd:minExclusive";
    public static final String XS_TAG = "xs:minExclusive";

    private XsdMinExclusive(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdMinExclusive(convertNodeMap(node.getAttributes())));
    }
}
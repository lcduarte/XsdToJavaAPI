package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAllVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:all element. Since it shares the same attributes as {@link XsdChoice} or {@link XsdSequence}
 * it extends {@link XsdMultipleElements}. For more information check {@link XsdMultipleElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_all.asp">xsd:all element definition and usage</a>
 */
public class XsdAll extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:all";
    public static final String XS_TAG = "xs:all";

    /**
     * {@link XsdAllVisitor} instance, which restricts his children elements to {@link XsdElement} instances.
     * Can also have {@link XsdAnnotation} children elements as per inheritance of {@link XsdAnnotatedElementsVisitor}
     */
    private final XsdAllVisitor visitor = new XsdAllVisitor(this);

    /**
     * Specifies the minimum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0. Default value is 1. This attribute cannot be used if the parent element is the
     * XsdSchema element.
     */
    private Integer minOccurs;

    /**
     * Specifies the maximum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0. Default value is 1. This attribute cannot be used if the parent element is the
     * XsdSchema element.
     */
    private Integer maxOccurs;

    private XsdAll(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam){
        super(parser, elementFieldsMapParam);
    }

    /**
     * Sets the occurs fields with the information provided in the Map object or with their default values.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.minOccurs = AttributeValidations.validateNonNegativeInteger(XSD_TAG, MIN_OCCURS_TAG, elementFieldsMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = AttributeValidations.validateNonNegativeInteger(XSD_TAG, MAX_OCCURS_TAG, elementFieldsMap.getOrDefault(MAX_OCCURS_TAG, "1"));
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdAllVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node) {
        return xsdParseSkeleton(node, new XsdAll(parser, convertNodeMap(node.getAttributes())));
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public Integer getMaxOccurs() {
        return maxOccurs;
    }
}

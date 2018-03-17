package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdReferenceElement;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdAbstractRestrictionChild;

import java.security.InvalidParameterException;

import static org.xmlet.xsdparser.xsdelements.XsdReferenceElement.NAME_TAG;

public abstract class ReferenceBase {

    private static final String REF = "ref";

    public abstract XsdAbstractElement getElement();

    /**
     * This method creates a ReferenceBase object that serves as a Wrapper to xsdelements.
     * If a XsdElement has a ref attribute it results in a UnsolvedReference object
     * Else it results in a ConcreteElement.
     * @param element The element which will be "wrapped"
     * @return A Wrapper for the element received
     */
    public static ReferenceBase createFromXsd(XsdAbstractElement element) {
        String ref = getRef(element);
        String name = getName(element);

        if (ref == null || name != null || element instanceof XsdAbstractRestrictionChild){
            return new ConcreteElement(element);
        } else {
            if (element instanceof XsdReferenceElement){
                return new UnsolvedReference((XsdReferenceElement) element);
            }

            throw new InvalidParameterException("The element should either have a name attribute or a ref attribute.");
        }
    }

    static String getName(XsdAbstractElement element){
        return getNodeValue(element, NAME_TAG);
    }

    static String getRef(XsdAbstractElement element){
        return getNodeValue(element, REF);
    }

    /**
     * @param element The element that contains the attributes
     * @param nodeName The attribute name that will be searched
     * @return The value of the attribute contained in element with the name nodeName
     */
    private static String getNodeValue(XsdAbstractElement element, String nodeName){
        return element.getElementFieldsMap().getOrDefault(nodeName, null);
    }
}

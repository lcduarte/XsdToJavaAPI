package XsdElements.ElementsWrapper;

import XsdElements.XsdElementBase;
import XsdElements.XsdReferenceElement;
import org.w3c.dom.Node;

import static XsdElements.XsdReferenceElement.NAME;

public abstract class ReferenceBase {

    private static final String REF = "ref";

    public abstract XsdElementBase getElement();

    /**
     * This method creates a ReferenceBase object that serves as a Wrapper to XsdElements.
     * If a XsdElement has a ref attribute it results in a UnsolvedReference object
     * Else it results in a ConcreteElement.
     * @param element The element which will be "wrapped"
     * @return A Wrapper for the element received
     */
    public static ReferenceBase createFromXsd(XsdElementBase element) {
        String ref = getRef(element);

        if (ref != null){
            if (element instanceof XsdReferenceElement){
                return new UnsolvedReference((XsdReferenceElement) element);
            }

            throw new RuntimeException("Invalid element parameter");
        } else {
            return new ConcreteElement(element);
        }
    }

    static String getName(XsdElementBase element){
        return getNodeValue(element, NAME);
    }

    static String getRef(XsdElementBase element){
        return getNodeValue(element, REF);
    }

    /**
     * @param element The element that contains the attributes
     * @param nodeName The attribute name that will be searched
     * @return The value of the attribute contained in element with the name nodeName
     */
    private static String getNodeValue(XsdElementBase element, String nodeName){
        Node nameNode = element.getNodeAttributes().getNamedItem(nodeName);

        return nameNode == null ? null : nameNode.getNodeValue();
    }
}

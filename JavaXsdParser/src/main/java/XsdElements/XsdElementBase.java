package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.Visitors.Visitor;
import XsdParser.XsdParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.List;

public abstract class XsdElementBase {

    private NamedNodeMap nodeAttributes;

    public static final String ID = "id";
    public static final String MIN_OCCURS = "maxOccurs";
    public static final String MAX_OCCURS = "minOccurs";

    private String id;
    private String maxOccurs;
    private String minOccurs;
    private XsdElementBase parent;

    /**
     * This method serves as a base to all XsdElements which need to set their class specific attributes
     * @param nodeAttributes The node containing all attributes of a XSDElement
     */
    public void setAttributes(NamedNodeMap nodeAttributes){
        this.nodeAttributes = nodeAttributes;

        this.id = nodeAttributes.getNamedItem(ID) == null ? null : nodeAttributes.getNamedItem(ID).getNodeValue();
        this.minOccurs = nodeAttributes.getNamedItem(MIN_OCCURS) == null ? null : nodeAttributes.getNamedItem(MIN_OCCURS).getNodeValue();
        this.maxOccurs = nodeAttributes.getNamedItem(MAX_OCCURS) == null ? null : nodeAttributes.getNamedItem(MAX_OCCURS).getNodeValue();
    }

    public NamedNodeMap getNodeAttributes() {
        return nodeAttributes;
    }

    public String getId() {
        return id;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public String getMinOccurs() {
        return minOccurs;
    }

    public abstract void accept(Visitor visitor);

    public abstract Visitor getVisitor();

    /**
     * @param node The node from where the element will be parsed
     * @param element The concrete element that will be populated and returned
     * @return A wrapper object that contains the parsed XSD object.
     */
    static ReferenceBase xsdParseSkeleton(Node node, XsdElementBase element){
        NamedNodeMap attributes = node.getAttributes();
        element.setAttributes(attributes);

        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                if (XsdParser.parseMapper.containsKey(nodeName)){
                    XsdParser.parseMapper.get(nodeName).apply(child).getElement().accept(element.getVisitor());
                }
            }

            child = child.getNextSibling();
        }

        return ReferenceBase.createFromXsd(element);
    }

    /**
     * This method iterates on the current element children and replaces any UnsolvedReference object that has a
     * ref attribute that matches a ConcreteElement name attribute
     * @param element A concrete element that will replace a unsolved reference, if existent
     */
    public void replaceUnsolvedElements(ConcreteElement element){
        List<ReferenceBase> elements = this.getElements();

        if (elements != null){
            elements.stream()
                .filter(referenceBase -> referenceBase instanceof UnsolvedReference)
                .map(referenceBase -> (UnsolvedReference) referenceBase)
                .filter(unsolvedReference -> unsolvedReference.getRef().equals(element.getName()))
                .findFirst()
                .ifPresent(oldElement -> elements.set(elements.indexOf(oldElement), element));
        }
    }

    public abstract List<ReferenceBase> getElements();

    public XsdElementBase getParent() {
        return parent;
    }

    void setParent(XsdElementBase parent) {
        this.parent = parent;
    }
}

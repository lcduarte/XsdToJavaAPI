package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.Visitors.Visitor;
import XsdParser.XsdParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class XsdElementBase {

    private HashMap<String, String> elementFieldsMap = new HashMap<>();

    private static final String ID = "id";
    private static final String MIN_OCCURS = "maxOccurs";
    private static final String MAX_OCCURS = "minOccurs";

    private String id;
    private String maxOccurs;
    private String minOccurs;
    private XsdElementBase parent;

    protected XsdElementBase(){

    }

    protected XsdElementBase(XsdElementBase parent) {
        setParent(parent);
    }

    protected XsdElementBase(HashMap<String, String> elementFieldsMap){
        setFields(elementFieldsMap);
    }

    protected XsdElementBase(XsdElementBase parent, HashMap<String, String> elementFieldsMap){
        setParent(parent);
        setFields(elementFieldsMap);
    }

    /**
     * This method serves as a base to all XsdElements which need to set their class specific attributes
     * @param elementFieldsMap The node map containing all attributes of a XSDElement
     */
    public void setFields(HashMap<String, String> elementFieldsMap){
        if (elementFieldsMap != null){
            this.elementFieldsMap = elementFieldsMap;

            this.id = elementFieldsMap.getOrDefault(ID, id);
            this.minOccurs = elementFieldsMap.getOrDefault(MIN_OCCURS, minOccurs);
            this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS, maxOccurs);
        }
    }

    public HashMap<String, String> getElementFieldsMap() {
        return elementFieldsMap;
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

    public abstract Visitor getVisitor();

    abstract void accept(Visitor visitor);

    public abstract XsdElementBase createCopyWithAttributes(HashMap<String, String> placeHolderAttributes);

    abstract List<ReferenceBase> getElements();

    public Stream<XsdElementBase> getXsdElements(){
        List<ReferenceBase> elements = getElements();

        if (elements == null){
            return null;
        }

        return elements.stream().filter(element -> element instanceof ConcreteElement).map(ReferenceBase::getElement);
    }

    /**
     * @param node The node from where the element will be parsed
     * @param element The concrete element that will be populated and returned
     * @return A wrapper object that contains the parsed XSD object.
     */
    static ReferenceBase xsdParseSkeleton(Node node, XsdElementBase element){
        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                Function<Node, ReferenceBase> parserFunction = XsdParser.parseMappers.get(nodeName);

                if (parserFunction != null){
                    parserFunction.apply(child).getElement().accept(element.getVisitor());
                }
            }

            child = child.getNextSibling();
        }

        return ReferenceBase.createFromXsd(element);
    }

    static HashMap<String, String> convertNodeMap(NamedNodeMap nodeMap){
        HashMap<String, String> attributesMapped = new HashMap<>();

        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            attributesMapped.put(node.getNodeName(), node.getNodeValue());
        }

        return attributesMapped;
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

    public XsdElementBase getParent() {
        return parent;
    }

    void setParent(XsdElementBase parent) {
        this.parent = parent;
    }

}

package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class XsdAbstractElement {

    protected Map<String, String> elementFieldsMap = new HashMap<>();

    static final String ID_TAG = "id";
    public static final String NAME_TAG = "name";
    static final String ABSTRACT_TAG = "abstract";
    static final String DEFAULT_ELEMENT_TAG = "defaultElement";
    protected static final String FIXED_TAG = "fixed";
    static final String TYPE_TAG = "type";
    static final String MIXED_TAG = "mixed";
    static final String BLOCK_TAG = "block";
    static final String FINAL_TAG = "final";
    static final String USE_TAG = "use";
    static final String SUBSTITUTION_GROUP_TAG = "substitutionGroup";
    static final String DEFAULT_TAG = "default";
    static final String FORM_TAG = "form";
    static final String NILLABLE_TAG = "nillable";
    static final String MIN_OCCURS_TAG = "minOccurs";
    static final String MAX_OCCURS_TAG = "maxOccurs";
    static final String ITEM_TYPE_TAG = "itemType";
    static final String BASE_TAG = "base";
    static final String SOURCE_TAG = "source";
    static final String XML_LANG_TAG = "xml:lang";
    static final String MEMBER_TYPES_TAG = "memberTypes";
    public static final String VALUE_TAG = "value";

    private XsdAbstractElement parent;

    protected XsdAbstractElement(Map<String, String> elementFieldsMap){
        setFields(elementFieldsMap);
    }

    protected XsdAbstractElement(XsdAbstractElement parent, Map<String, String> elementFieldsMap){
        setParent(parent);
        setFields(elementFieldsMap);
    }

    /**
     * This method serves as a base to all xsdelements which need to set their class specific attributes.
     * @param elementFieldsMap The node map containing all attributes of a XSDElement
     */
    public void setFields(Map<String, String> elementFieldsMap){
        if (elementFieldsMap != null){
            this.elementFieldsMap = elementFieldsMap;
        }
    }

    public Map<String, String> getElementFieldsMap() {
        return elementFieldsMap;
    }

    /**
     * Obtains the visitor of an XsdAbstractElement instance.
     * @return The concrete visitor instance.
     */
    public abstract XsdElementVisitor getXsdElementVisitor();

    public abstract void accept(XsdElementVisitor xsdElementVisitor);

    /**
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A deep copy of the object from which is called upon.
     */
    public abstract XsdAbstractElement clone(Map<String, String> placeHolderAttributes);

    protected abstract List<ReferenceBase> getElements();

    public Stream<XsdAbstractElement> getXsdElements(){
        List<ReferenceBase> elements = getElements();

        if (elements == null){
            return new ArrayList<XsdAbstractElement>().stream();
        }

        return elements.stream().filter(element -> element instanceof ConcreteElement).map(ReferenceBase::getElement);
    }

    /**
     * The base code for parsing an XsdAbstractElement. All the concrete implementations of this
     * class should call this method in order to parse its contents.
     * @param node The node from where the element will be parsed
     * @param element The concrete element that will be populated and returned
     * @return A wrapper object that contains the parsed XSD object.
     */
    static ReferenceBase xsdParseSkeleton(Node node, XsdAbstractElement element){
        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                Function<Node, ReferenceBase> parserFunction = XsdParser.getParseMappers().get(nodeName);

                if (parserFunction != null){
                    parserFunction.apply(child).getElement().accept(element.getXsdElementVisitor());
                }
            }

            child = child.getNextSibling();
        }

        return ReferenceBase.createFromXsd(element);
    }

    protected static Map<String, String> convertNodeMap(NamedNodeMap nodeMap){
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

    /**
     * @return The parent of the current XsdAbstractElement.
     */
    public XsdAbstractElement getParent() {
        return parent;
    }

    protected void setParent(XsdAbstractElement parent) {
        this.parent = parent;
    }

    static String xsdRawContentParse(Node node) {
        Node child = node.getFirstChild();
        StringBuilder stringBuilder = new StringBuilder();

        while (child != null) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                stringBuilder.append(child.getTextContent());
            }

            child = child.getNextSibling();
        }

        return stringBuilder.toString();
    }
}

import XsdElements.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XsdMain {

    private static HashMap<String, Function<Node, XsdElementBase>> Mapper = new HashMap<>();
    private static List<XsdElementBase> Elements = new ArrayList<>();
    private static List<XsdElementBase> ToSolveElements = new ArrayList<>();
    private static HashMap<String, XsdElementBase> NamedElements = new HashMap<>();
    private static HashMap<String, XsdAttributeGroup> NamedAttributeGroups = new HashMap<>();
    private static HashMap<XsdElementBase, List<XsdAttributeGroup>> UnsolvedAttributeGroups = new HashMap<>();

    public static void main(String argv[]) {

        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            URL resourceUrl = XsdMain.class.getClassLoader().getResource("html_5.xsd");

            if (resourceUrl == null) {
                throw new Exception("html_5.xsd file missing from resources.");
            }

            File xsdFile = new File(resourceUrl.getPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xsdFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nodes = doc.getFirstChild().getChildNodes();

            System.out.println("----------------------------");

            Mapper.put("xsd:all", XsdMain::parseXsdAll);
            Mapper.put("xsd:attribute", XsdMain::parseXsdAttribute);
            Mapper.put("xsd:attributeGroup", XsdMain::parseXsdAttributeGroup);
            Mapper.put("xsd:element", XsdMain::parseXsdElement);
            Mapper.put("xsd:group", XsdMain::parseXsdGroup);
            Mapper.put("xsd:choice", XsdMain::parseXsdChoice);
            Mapper.put("xsd:sequence", XsdMain::parseXsdSequence);
            Mapper.put("xsd:complexType", XsdMain::parseXsdComplexType);

            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                String nodeName = node.getNodeName();

                //System.out.println("Current Element :" + nodeName);

                if (node.getNodeType() == Node.ELEMENT_NODE && Mapper.containsKey(nodeName)) {
                    Mapper.get(nodeName).apply(node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resolveRefs();

        List<XsdElementBase> unresolvedRefs = Elements.stream()
                .filter(elementBase ->
                        elementBase instanceof XsdReferenceElement && ((XsdReferenceElement)elementBase).getRef() != null)
                .collect(Collectors.toList());

        unresolvedRefs.forEach(elementBase -> {
            if (elementBase instanceof XsdReferenceElement){
                System.out.println(((XsdReferenceElement)elementBase).getRef());
            }
        });

        System.out.println("Número de unresolved : " + unresolvedRefs.size());

        System.out.println("Número de elementos obtidos : " + Elements.size());

        System.out.println("Done");
    }

    //region Reference resolving
    private static void resolveRefs() {
        ToSolveElements.forEach((elementBase -> {
            if (elementBase instanceof XsdComplexType){
                XsdComplexType complexElement = (XsdComplexType) elementBase;
                XsdElementBase complexChildElement = complexElement.getChildElement();

                if (complexChildElement != null && complexChildElement instanceof XsdGroup){
                    XsdGroup groupElement = (XsdGroup) complexChildElement;

                    if (groupElement.getRef() != null){
                        complexElement.setChildElement((XsdGroup) NamedElements.get(groupElement.getRef()));
                    }
                }

                complexElement.addAttributes(resolveAttributeGroupRefs(complexElement));
            }

            if (elementBase instanceof XsdGroup){
                resolveRefsFromChildElements(((XsdGroup) elementBase).getChildElement());
            }

            if (elementBase instanceof XsdMultipleElements){
                resolveRefsFromChildElements((XsdMultipleElements) elementBase);
            }

            if (elementBase instanceof XsdAttributeGroup){
                XsdAttributeGroup attributeGroup = (XsdAttributeGroup) elementBase;

                attributeGroup.addAttributes(resolveAttributeGroupRefs(attributeGroup));
            }
        }));
    }

    private static List<XsdAttribute> resolveAttributeGroupRefs(XsdElementBase attributeGroupContainer) {
        List<XsdAttribute> attributes = new ArrayList<>();
        List<XsdAttributeGroup> containedGroups = UnsolvedAttributeGroups.get(attributeGroupContainer);

        containedGroups.forEach(attributeGroup ->
            attributes.addAll(NamedAttributeGroups.get(attributeGroup.getRef()).getAttributes())
        );

        return attributes;
    }

    private static void resolveRefsFromChildElements(XsdMultipleElements multipleElementsContainer) {
        List<XsdElementBase> elements = multipleElementsContainer.getElements();

        for (int i = 0; i < elements.size(); ++i){
            XsdElementBase baseElement = elements.get(i);

            if (baseElement instanceof XsdReferenceElement){
                XsdReferenceElement referenceElement = (XsdReferenceElement) baseElement;

                String ref = referenceElement.getRef();

                if (ref != null && NamedElements.containsKey(ref)){
                    multipleElementsContainer.replaceElement(i, NamedElements.get(ref));
                }
            }
        }
    }

    //endregion

    //region XsdTypes

    private static XsdElementBase parseXsdAll(Node node) {
        XsdAll allElement = new XsdAll(node);

        return xsdParseSkeleton(node, allElement, (allChild) -> {
            if (allChild instanceof XsdElement){
                allElement.addElement((XsdElement) allChild);
            }
        });
    }

    private static XsdElementBase parseXsdElement(Node node){
        XsdElement element = new XsdElement(node);

        return xsdParseSkeleton(node, element, (elementChild) -> {
            if (elementChild instanceof XsdComplexType){
                element.setComplexType((XsdComplexType) elementChild);
            }
        });
    }

    private static XsdElementBase parseXsdGroup(Node node){
        XsdGroup groupElement = new XsdGroup(node);

        xsdParseSkeleton(node, groupElement, (groupChild) -> {
            if (groupChild instanceof XsdMultipleElements){
                groupElement.setChildElement((XsdMultipleElements) groupChild);
            }
        });

        if (groupElement.getChildElement() != null &&
                groupElement.getChildElement()
                            .getElements()
                            .stream()
                            .anyMatch(elementObj ->
                                elementObj instanceof XsdReferenceElement && ((XsdReferenceElement) elementObj).getRef() != null
                            )){
            ToSolveElements.add(groupElement);
        }

        return groupElement;
    }

    private static XsdElementBase parseXsdChoice(Node node){
        XsdChoice choiceElement = new XsdChoice(node);

        return xsdParseSkeleton(node, choiceElement, (choiceChild) -> {
            if (choiceChild instanceof XsdElement){
                choiceElement.addElement((XsdElement) choiceChild);
            }

            if (choiceChild instanceof XsdGroup){
                choiceElement.addElement((XsdGroup) choiceChild);
            }

            if (choiceChild instanceof XsdChoice){
                choiceElement.addElement((XsdChoice) choiceChild);
            }

            if (choiceChild instanceof XsdSequence){
                choiceElement.addElement((XsdSequence) choiceChild);
            }
        });
    }

    private static XsdElementBase parseXsdSequence(Node node){
        XsdSequence sequenceElement = new XsdSequence(node);

        return xsdParseSkeleton(node, sequenceElement, (sequenceChild) -> {
            if (sequenceChild instanceof XsdElement){
                sequenceElement.addElement((XsdElement) sequenceChild);
            }

            if (sequenceChild instanceof XsdGroup){
                sequenceElement.addElement((XsdGroup) sequenceChild);
            }

            if (sequenceChild instanceof XsdChoice){
                sequenceElement.addElement((XsdChoice) sequenceChild);
            }

            if (sequenceChild instanceof XsdSequence){
                sequenceElement.addElement((XsdSequence) sequenceChild);
            }
        });
    }

    private static XsdElementBase parseXsdComplexType(Node node){
        XsdComplexType complexElement = new XsdComplexType(node);

        List<XsdAttributeGroup> unsolvedAttributeGroupsList = new ArrayList<>();

        xsdParseSkeleton(node, complexElement, (complexTypeChild) -> {
            if (complexTypeChild instanceof XsdMultipleElements){
                complexElement.setChildElement((XsdMultipleElements) complexTypeChild);
            }

            if (complexTypeChild instanceof XsdGroup){
                complexElement.setChildElement((XsdGroup) complexTypeChild);

                if (((XsdGroup)complexTypeChild).getRef() != null){
                    if (!ToSolveElements.contains(complexElement)){
                        ToSolveElements.add(complexElement);
                    }
                }
            }

            if (complexTypeChild instanceof XsdAttribute){
                complexElement.addAttributes((XsdAttribute) complexTypeChild);
            }

            if (complexTypeChild instanceof XsdAttributeGroup){
                XsdAttributeGroup attributeGroup = (XsdAttributeGroup) complexTypeChild;

                if (attributeGroup.getRef() == null){
                    complexElement.addAttributes(attributeGroup.getAttributes());

                    NamedAttributeGroups.put(attributeGroup.getName(), attributeGroup);
                } else {
                    unsolvedAttributeGroupsList.add(attributeGroup);

                    if (!ToSolveElements.contains(complexElement)){
                        ToSolveElements.add(complexElement);
                    }
                }
            }
        });

        UnsolvedAttributeGroups.put(complexElement, unsolvedAttributeGroupsList);

        return complexElement;
    }

    private static XsdElementBase parseXsdAttribute(Node node) {
        // TODO Still missing the parsing of contained elements such as SimpleTypes.

        return new XsdAttribute(node);
    }

    private static XsdElementBase parseXsdAttributeGroup(Node node) {
        XsdAttributeGroup attributeGroupElement = new XsdAttributeGroup(node);

        List<XsdAttributeGroup> unsolvedAttributeGroupsList = new ArrayList<>();

        xsdParseSkeleton(node, attributeGroupElement, (childAttribute -> {
            if (childAttribute instanceof XsdAttribute){
                attributeGroupElement.addAttributes((XsdAttribute) childAttribute);
            }

            if (childAttribute instanceof XsdAttributeGroup){
                XsdAttributeGroup childAttributeGroup = (XsdAttributeGroup) childAttribute;

                if (childAttributeGroup.getRef() == null && childAttributeGroup.getName() != null){
                    attributeGroupElement.addAttributes(childAttributeGroup.getAttributes());

                    NamedAttributeGroups.put(childAttributeGroup.getName(), childAttributeGroup);
                } else {
                    if (!ToSolveElements.contains(attributeGroupElement)){
                        ToSolveElements.add(attributeGroupElement);
                    }

                    unsolvedAttributeGroupsList.add(childAttributeGroup);
                }
            }
        }));

        if (attributeGroupElement.getRef() == null && attributeGroupElement.getName() != null){
            NamedAttributeGroups.put(attributeGroupElement.getName(), attributeGroupElement);
        }

        UnsolvedAttributeGroups.put(attributeGroupElement, unsolvedAttributeGroupsList);

        return attributeGroupElement;
    }

    //endregion

    private static XsdElementBase xsdParseSkeleton(Node node, XsdElementBase element, Consumer<XsdElementBase> childConsumer){
        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                if (Mapper.containsKey(nodeName)){
                    childConsumer.accept(Mapper.get(nodeName).apply(child));
                }
            }

            child = child.getNextSibling();
        }

        addElementToPool(element);

        return element;
    }

    private static void addElementToPool(XsdElementBase element) {
        String ref = null;

        if (element instanceof XsdReferenceElement){
            XsdReferenceElement referenceElement = (XsdReferenceElement)element;

            String elementName = referenceElement.getName();

            if (elementName != null){
                NamedElements.put(elementName, element);
            }

            ref = referenceElement.getRef();
        }

        if (ref == null){
            Elements.add(element);
        }

        if (element instanceof XsdMultipleElements){
            checkChildrenUnsolvedRefs((XsdMultipleElements) element);
        }
    }

    private static void checkChildrenUnsolvedRefs(XsdMultipleElements multipleElementsElement) {
        if (multipleElementsElement.getElements()
                .stream()
                .anyMatch(element -> element instanceof XsdReferenceElement && ((XsdReferenceElement) element).getRef() != null)){
            ToSolveElements.add(multipleElementsElement);
        }
    }

}
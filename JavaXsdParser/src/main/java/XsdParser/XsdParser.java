package XsdParser;

import XsdElements.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XsdParser {

    public static HashMap<String, Function<Node, XsdElementBase>> parseMapper;
    private static HashMap<Class, BiConsumer<XsdElementBase, XsdReferenceElement>> referenceSolverMapper;
    private static XsdParser instance;

    private List<XsdElementBase> elements = new ArrayList<>();
    private List<XsdReferenceElement> unsolvedElements = new ArrayList<>();

    static {
        parseMapper = new HashMap<>();
        referenceSolverMapper = new HashMap<>();

        parseMapper.put(XsdAll.TAG, XsdAll::parse);
        parseMapper.put(XsdAttribute.TAG, XsdAttribute::parse);
        parseMapper.put(XsdAttributeGroup.TAG, XsdAttributeGroup::parse);
        parseMapper.put(XsdChoice.TAG, XsdChoice::parse);
        parseMapper.put(XsdComplexType.TAG, XsdComplexType::parse);
        parseMapper.put(XsdElement.TAG, XsdElement::parse);
        parseMapper.put(XsdGroup.TAG, XsdGroup::parse);
        parseMapper.put(XsdSequence.TAG, XsdSequence::parse);

        referenceSolverMapper.put(XsdChoice.class, XsdMultipleElements::replaceReferenceElement);
        referenceSolverMapper.put(XsdAll.class, XsdMultipleElements::replaceReferenceElement);
        referenceSolverMapper.put(XsdSequence.class, XsdMultipleElements::replaceReferenceElement);
        referenceSolverMapper.put(XsdGroup.class, XsdGroup::replaceReferenceElement);
        referenceSolverMapper.put(XsdComplexType.class, XsdComplexType::replaceReferenceElement);
        referenceSolverMapper.put(XsdAttributeGroup.class, XsdAttributeGroup::replaceReferenceElement);
    }

    public XsdParser(){
        instance = this;
    }

    public List<XsdElement> parse(String filePath) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            URL resourceUrl = XsdParser.class.getClassLoader().getResource(filePath);

            if (resourceUrl == null) {
                throw new Exception("html_5.xsd file missing from resources.");
            }

            File xsdFile = new File(resourceUrl.getPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xsdFile);

            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getFirstChild().getChildNodes();

            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                String nodeName = node.getNodeName();

                if (node.getNodeType() == Node.ELEMENT_NODE && parseMapper.containsKey(nodeName)) {
                    elements.add(parseMapper.get(nodeName).apply(node));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resolveRefs();

        return elements.stream()
                .filter(elementBase -> elementBase instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());
    }

    private void resolveRefs() {
        HashMap<String, XsdReferenceElement> namedElements = new HashMap<>();

        List<XsdReferenceElement> referenceElements = elements.stream()
                .filter(elementBase -> elementBase instanceof XsdReferenceElement)
                .map(elementBase -> (XsdReferenceElement) elementBase)
                .collect(Collectors.toList());

        referenceElements.stream()
                .filter(referenceElement -> referenceElement.getName() != null)
                .forEach(referenceElement -> namedElements.put(referenceElement.getName(), referenceElement));

        unsolvedElements.stream()
                .filter(referenceElement -> referenceElement.getRef() != null)
                .forEach(oldReferenceElement -> {
                    if (namedElements.containsKey(oldReferenceElement.getRef())){
                        XsdReferenceElement newReferenceElement = namedElements.get(oldReferenceElement.getRef());
                        newReferenceElement.setAttributes(oldReferenceElement.getNodeAttributes());

                        referenceSolverMapper.get(oldReferenceElement.getParent().getClass())
                                .accept(oldReferenceElement.getParent(), newReferenceElement);
                    } else {
                        System.err.println(oldReferenceElement.getRef());
                    }
                });
    }

    public static XsdParser getInstance(){
        return instance;
    }

    public void addUnsolvedReference(XsdReferenceElement referenceElement){
        unsolvedElements.add(referenceElement);
    }
}
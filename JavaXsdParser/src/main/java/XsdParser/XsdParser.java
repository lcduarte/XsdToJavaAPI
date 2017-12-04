package XsdParser;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XsdParser {

    public static HashMap<String, Function<Node, ReferenceBase>> parseMapper;
    private static XsdParser instance;

    private List<ReferenceBase> elements = new ArrayList<>();
    private List<UnsolvedReference> unsolvedElements = new ArrayList<>();
    private Map<String, Map<UnsolvedReference, List<XsdElementBase>>> parserUnsolvedElementsMap = new HashMap<>();

    static {
        parseMapper = new HashMap<>();

        parseMapper.put(XsdAll.TAG, XsdAll::parse);
        parseMapper.put(XsdAttribute.TAG, XsdAttribute::parse);
        parseMapper.put(XsdAttributeGroup.TAG, XsdAttributeGroup::parse);
        parseMapper.put(XsdChoice.TAG, XsdChoice::parse);
        parseMapper.put(XsdComplexType.TAG, XsdComplexType::parse);
        parseMapper.put(XsdElement.TAG, XsdElement::parse);
        parseMapper.put(XsdGroup.TAG, XsdGroup::parse);
        parseMapper.put(XsdSequence.TAG, XsdSequence::parse);
    }

    public XsdParser(){
        instance = this;
    }

    /**
     * Parses a Xsd file and all the contained elements. This code iterates on the nodes and parses
     * the supported ones. The supported types are identified by their TAG, in parseMapper which maps
     * the xsd tag to a function that parses that xsd type.
     * @param filePath The file path to the xsd file.
     * @return A list of parsed wrapped xsd elements.
     */
    public List<XsdElementBase> parse(String filePath) {
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

        resolveRefs(filePath);

        return elements.stream()
                        .filter(element -> element instanceof ConcreteElement)
                        .map(ReferenceBase::getElement)
                        .collect(Collectors.toList());
    }

    /**
     * This method resolves all the remaining UnsolvedReferences. It starts by gathering all the named
     * elements and then iterates on the unsolvedElements List in order to find if any of the unsolvedReferences
     * can be solved by replacing the unsolvedElement by its matching ConcreteElement, present in the
     * concreteElementsMap. The unsolvedReference matches a ConcreteElement by having its ref attribute
     * with the same value as the name attribute of the ConcreteElement.
     * @param filePath
     */
    private void resolveRefs(String filePath) {
        HashMap<String, ConcreteElement> concreteElementsMap = new HashMap<>();

        List<ConcreteElement> concreteElements = elements.stream()
                .filter(concreteElement -> concreteElement instanceof ConcreteElement)
                .map(concreteElement -> (ConcreteElement) concreteElement)
                .collect(Collectors.toList());

        concreteElements
                .forEach(referenceElement -> concreteElementsMap.put(referenceElement.getName(), referenceElement));

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < unsolvedElements.size(); i++) {
            replaceUnsolvedReference(concreteElementsMap, unsolvedElements.get(i), filePath);
        }
    }

    private void replaceUnsolvedReference(HashMap<String, ConcreteElement> concreteElementsMap, UnsolvedReference unsolvedReference, String filePath) {
        if (concreteElementsMap.containsKey(unsolvedReference.getRef())){
            HashMap<String, String> oldElementAttributes = unsolvedReference.getElement().getElementFieldsMap();

            if (oldElementAttributes == null){
                oldElementAttributes = new HashMap<>();
            }

            XsdElementBase substitutionElement = concreteElementsMap.get(unsolvedReference.getRef())
                                                                    .getElement()
                                                                    .createCopyWithAttributes(oldElementAttributes);

            ConcreteElement substitutionElementWrapper = (ConcreteElement) ReferenceBase.createFromXsd(substitutionElement);

            unsolvedReference.getParent().getElement().replaceUnsolvedElements(substitutionElementWrapper);
        } else {
            if (!parserUnsolvedElementsMap.containsKey(filePath)){
                addNewFileUnsolvedEntry(filePath, unsolvedReference);
            } else {
                addNewUnsolvedEntry(filePath, unsolvedReference);
            }
        }
    }

    private void addNewFileUnsolvedEntry(String filePath, UnsolvedReference unsolvedReference) {
        Map<UnsolvedReference, List<XsdElementBase>> entry = new HashMap<>();

        List<XsdElementBase> parents = new ArrayList<>();

        parents.add(unsolvedReference.getParent().getElement());

        entry.put(unsolvedReference, parents);

        parserUnsolvedElementsMap.put(filePath, entry);
    }

    private void addNewUnsolvedEntry(String filePath, UnsolvedReference unsolvedReference) {
        Map<UnsolvedReference, List<XsdElementBase>> entry = parserUnsolvedElementsMap.get(filePath);

        Optional<UnsolvedReference> innerEntry = entry.keySet().stream()
                .filter(unsolvedReferenceObj -> unsolvedReferenceObj.getRef().equals(unsolvedReference.getRef()))
                .findFirst();

        if (innerEntry.isPresent()){
            innerEntry.ifPresent(unsolvedReferenceObj -> entry.get(unsolvedReferenceObj).add(unsolvedReference.getParent().getElement()));
        } else {
            List<XsdElementBase> parents = new ArrayList<>();

            parents.add(unsolvedReference.getParent().getElement());

            parserUnsolvedElementsMap.get(filePath).put(unsolvedReference, parents);
        }
    }

    Map<UnsolvedReference, List<XsdElementBase>> getUnsolvedReferencesForFile(String filePath){
        return parserUnsolvedElementsMap.getOrDefault(filePath, new HashMap<>());
    }

    public static XsdParser getInstance(){
        return instance;
    }

    public void addUnsolvedReference(UnsolvedReference unsolvedReference){
        unsolvedElements.add(unsolvedReference);
    }
}
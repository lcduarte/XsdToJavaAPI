package XsdElements;

import XsdParser.XsdParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class XsdElementBase {

    private NamedNodeMap nodeAttributes;

    private String id;
    private String maxOccurs;
    private String minOccurs;

    public void setAttributes(NamedNodeMap attributes){
        this.nodeAttributes = attributes;

        this.id = attributes.getNamedItem("id") == null ? null : attributes.getNamedItem("id").getNodeValue();
        this.maxOccurs = attributes.getNamedItem("maxOccurs") == null ? null : attributes.getNamedItem("maxOccurs").getNodeValue();
        this.minOccurs = attributes.getNamedItem("minOccurs") == null ? null : attributes.getNamedItem("minOccurs").getNodeValue();
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

    static XsdElementBase xsdParseSkeleton(Node node, XsdElementBase element){
        element.setAttributes(node.getAttributes());

        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                if (XsdParser.parseMapper.containsKey(nodeName)){
                    XsdParser.parseMapper.get(nodeName).apply(child).accept(element.getVisitor());
                }
            }

            child = child.getNextSibling();
        }

        return element;
    }
}

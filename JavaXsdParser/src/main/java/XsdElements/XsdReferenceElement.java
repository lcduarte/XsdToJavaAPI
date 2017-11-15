package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class XsdReferenceElement extends XsdElementBase {

    private String name;
    private String ref;

    XsdReferenceElement(Node node) {
        super(node);

        NamedNodeMap attributes = node.getAttributes();

        this.name = attributes.getNamedItem("name") == null ? null : attributes.getNamedItem("name").getNodeValue();
        this.ref = attributes.getNamedItem("ref") == null ? null : attributes.getNamedItem("ref").getNodeValue();
    }

    public String getName(){
        return name;
    }

    public String getRef(){
        return ref;
    }

}

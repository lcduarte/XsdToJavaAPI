package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XsdElementBase {

    private String id;
    //private String maxOccurs;
    //private String minOccurs;

    XsdElementBase(Node node){
        NamedNodeMap attributes = node.getAttributes();

        this.id = attributes.getNamedItem("id") == null ? null : attributes.getNamedItem("id").getNodeValue();
    }

    public String getId() {
        return id;
    }

}

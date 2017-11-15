package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XsdComplexType extends XsdElementBase {

    private XsdElementBase childElement;

    private String name;
    private String elementAbstract;
    private String mixed;
    private String block;
    private String elementFinal;

    public XsdComplexType(Node node) {
        super(node);

        NamedNodeMap attributes = node.getAttributes();

        this.name = attributes.getNamedItem("name") == null ? null : attributes.getNamedItem("name").getNodeValue();
        this.elementAbstract = attributes.getNamedItem("abstract") == null ? null : attributes.getNamedItem("abstract").getNodeValue();
        this.mixed = attributes.getNamedItem("mixed") == null ? null : attributes.getNamedItem("mixed").getNodeValue();
        this.block = attributes.getNamedItem("block") == null ? null : attributes.getNamedItem("block").getNodeValue();
        this.elementFinal = attributes.getNamedItem("final") == null ? null : attributes.getNamedItem("final").getNodeValue();
    }

    public void setChildElement(XsdMultipleElements element){
        this.childElement = element;
    }

    public void setChildElement(XsdGroup element){
        this.childElement = element;
    }

    public XsdElementBase getChildElement() {
        return childElement;
    }

    public String getName() {
        return name;
    }

    public String getAbstract(){
        return elementAbstract;
    }

    public String getMixed() {
        return mixed;
    }

    public String getBlock() {
        return block;
    }

    public String getFinal() {
        return elementFinal;
    }
}

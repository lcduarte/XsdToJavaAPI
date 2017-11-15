package XsdElements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XsdElement extends XsdReferenceElement {

    private XsdComplexType complexType;

    private String type;
    //private String substitutionGroup;
    //private String default;
    //private String fixed;
    //private String form;
    //private String nillable;
    //private String abstract;
    //private String block;
    //private String final;

    public XsdElement(Node node){
        super(node);

        NamedNodeMap attributes = node.getAttributes();

        this.type = attributes.getNamedItem("type") == null ? null : attributes.getNamedItem("type").getNodeValue();
    }

    public void setComplexType(XsdComplexType complexType) {
        this.complexType = complexType;
    }

    public XsdComplexType getComplexType() {
        return complexType;
    }

    public String getType(){
        return type;
    }

}

package XsdElements;

import org.w3c.dom.NamedNodeMap;

public abstract class XsdReferenceElement extends XsdElementBase {

    private XsdElementBase parent;

    private String name;
    private String ref;

    @Override
    public void setAttributes(NamedNodeMap attributes) {
        super.setAttributes(attributes);

        if (this.name == null){
            this.name = attributes.getNamedItem("name") == null ? null : attributes.getNamedItem("name").getNodeValue();
            this.ref = attributes.getNamedItem("ref") == null ? null : attributes.getNamedItem("ref").getNodeValue();
        }
    }

    public String getName(){
        return name;
    }

    public String getRef(){
        return ref;
    }

    public XsdElementBase getParent() {
        return parent;
    }

    void setParent(XsdElementBase parent) {
        this.parent = parent;
    }
}

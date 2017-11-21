package XsdElements;

import org.w3c.dom.NamedNodeMap;

public abstract class XsdReferenceElement extends XsdElementBase {

    private XsdElementBase parent;

    public static final String NAME = "name";
    public static final String REF = "ref";

    private String name;
    private String ref;

    @Override
    public void setAttributes(NamedNodeMap attributes) {
        super.setAttributes(attributes);

        if (this.name == null){
            this.name = attributes.getNamedItem(NAME) == null ? null : attributes.getNamedItem(NAME).getNodeValue();
            this.ref = attributes.getNamedItem(REF) == null ? null : attributes.getNamedItem(REF).getNodeValue();
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

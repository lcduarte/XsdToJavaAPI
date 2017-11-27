package XsdElements;

import org.w3c.dom.NamedNodeMap;

/**
 * This class is an abstraction of all classes that can have a ref attribute, which helps
 * distinguish those from the other XsdElements
 */
public abstract class XsdReferenceElement extends XsdElementBase {

    public static final String NAME = "name";

    private String name;

    @Override
    public void setAttributes(NamedNodeMap attributes) {
        super.setAttributes(attributes);

        if (this.name == null){
            this.name = attributes.getNamedItem(NAME) == null ? null : attributes.getNamedItem(NAME).getNodeValue();
        }
    }

    public String getName() {
        return name;
    }

}

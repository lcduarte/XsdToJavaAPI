package XsdElements;

import java.util.HashMap;

/**
 * This class is an abstraction of all classes that can have a ref attribute, which helps
 * distinguish those from the other XsdElements
 */
public abstract class XsdReferenceElement extends XsdElementBase {

    public static final String NAME = "name";

    private String name;

    XsdReferenceElement(XsdElementBase parent, HashMap<String, String> elementFieldsMap) {
        super(parent);

        setFields(elementFieldsMap);
    }

    XsdReferenceElement(HashMap<String, String> elementFieldsMap) {
        setFields(elementFieldsMap);
    }

    XsdReferenceElement(XsdElementBase parent) {
        super(parent);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        if (elementFieldsMap != null){
            super.setFields(elementFieldsMap);

            this.name = elementFieldsMap.getOrDefault(NAME, name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package org.xmlet.xsdparser.xsdelements;

import java.util.Map;

/**
 * This class is an abstraction of all classes that can have a ref/name attribute, which helps
 * distinguish those from the other xsdelements
 */
public abstract class XsdReferenceElement extends XsdAnnotatedElements {

    private String name;

    XsdReferenceElement(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    /**
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A deep copy of the object from which is called upon.
     */
    public abstract XsdReferenceElement clone(Map<String, String> placeHolderAttributes);

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.name = elementFieldsMap.getOrDefault(NAME_TAG, name);
        }
    }

    /**
     * @return The name of the element, with all the special characters replaced with the '_' char.
     */
    public String getName() {
        return name == null ? null : name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    public void setName(String name) {
        this.name = name;
    }
}

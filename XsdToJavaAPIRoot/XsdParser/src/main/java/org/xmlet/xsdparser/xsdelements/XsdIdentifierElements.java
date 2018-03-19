package org.xmlet.xsdparser.xsdelements;

import java.util.Map;

public abstract class XsdIdentifierElements extends XsdAbstractElement {

    private String id;

    XsdIdentifierElements(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.id = elementFieldsMap.getOrDefault(ID_TAG, id);
        }
    }

    public String getId() {
        return id;
    }
}

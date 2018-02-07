package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdEnumeration extends XsdAbstractRestrictionChild{

    public static String XSD_TAG = "xsd:enumeration";
    public static String XS_TAG = "xs:enumeration";

    private String value;

    public XsdEnumeration(String value){
        this.value = value;
    }

    private XsdEnumeration(HashMap<String, String> elementFieldsMap) {
        setFields(elementFieldsMap);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        if (elementFieldsMap != null){
            value = elementFieldsMap.getOrDefault(VALUE, value);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdEnumeration(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdEnumeration clone(HashMap<String, String> placeHolderAttributes) {
        return new XsdEnumeration(this.value);
    }

    public String getValue() {
        return value;
    }

}

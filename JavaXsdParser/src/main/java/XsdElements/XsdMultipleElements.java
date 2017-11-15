package XsdElements;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class XsdMultipleElements extends XsdElementBase {

    private List<XsdElementBase> elements;

    XsdMultipleElements(Node node){
        super(node);
        this.elements = new ArrayList<>();
    }

    public void replaceElement(int elementIndex, XsdElementBase newElement){
        elements.set(elementIndex, newElement);
    }

    void addElement(XsdElementBase element){
        this.elements.add(element);
    }

    public List<XsdElementBase> getElements(){
        return elements;
    }
}

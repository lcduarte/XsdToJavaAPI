package XsdElements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class XsdMultipleElements extends XsdElementBase {

    private List<XsdElementBase> elements = new ArrayList<>();

    private void replaceElement(int elementIndex, XsdElementBase newElement){
        elements.set(elementIndex, newElement);
    }

    void addElement(XsdElementBase element){
        this.elements.add(element);
    }

    public List<XsdElementBase> getElements(){
        return elements;
    }

    public static void replaceReferenceElement(XsdElementBase elementBase, XsdReferenceElement referenceElement) {
        if (elementBase instanceof XsdMultipleElements){
            XsdMultipleElements element = (XsdMultipleElements) elementBase;

            Optional<XsdReferenceElement> oldElement = element.getElements().stream()
                    .filter(elementBaseObj -> elementBaseObj instanceof XsdReferenceElement)
                    .map(referenceElementObj -> (XsdReferenceElement) referenceElementObj)
                    .filter(referenceElementObj -> referenceElementObj.getRef() != null)
                    .filter(referenceElementObj ->  referenceElementObj.getRef().equals(referenceElement.getName()))
                    .findFirst();

            if (oldElement.isPresent()){
                int idx = element.getElements().indexOf(oldElement.get());

                element.replaceElement(idx, referenceElement);
            } else {
                System.err.println("Não devia estar aqui");
            }
        }
    }
}

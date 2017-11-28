package XsdParser;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.XsdChoice;
import XsdElements.XsdComplexType;
import XsdElements.XsdElement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class XsdParserTest {

    private static final List<ReferenceBase> elements;

    static{
        XsdParser parser = new XsdParser();

        elements = parser.parse("html_5.xsd")
                         .stream()
                         .filter(element -> element.getElement() instanceof XsdElement)
                         .collect(Collectors.toList());
    }

    @Test
    public void testElementCount() throws Exception {
        Assert.assertEquals(104, elements.size());
    }

    @Test
    public void testFirstElementContents() throws Exception {
        ReferenceBase firstElement = elements.get(0);

        Assert.assertEquals(firstElement.getClass(), ConcreteElement.class);

        ConcreteElement concreteFirstElement = (ConcreteElement) firstElement;

        Assert.assertEquals(concreteFirstElement.getElement().getClass(), XsdElement.class);

        XsdElement htmlElement = (XsdElement) concreteFirstElement.getElement();

        Assert.assertEquals("html", htmlElement.getName());

        XsdComplexType firstElementChild = htmlElement.getComplexType();

        Assert.assertEquals(firstElementChild.getChildElement().getElement().getClass(), XsdChoice.class);

        XsdChoice complexTypeChild = (XsdChoice) firstElementChild.getChildElement().getElement();

        List<ReferenceBase> choiceElements = complexTypeChild.getElements();

        Assert.assertEquals(2, choiceElements.size());

        ReferenceBase choiceElement1Obj = choiceElements.get(0);
        ReferenceBase choiceElement2Obj = choiceElements.get(1);

        Assert.assertEquals(choiceElement1Obj.getClass(), ConcreteElement.class);
        Assert.assertEquals(choiceElement2Obj.getClass(), ConcreteElement.class);

        ConcreteElement concreteChild1 = (ConcreteElement) choiceElement1Obj;
        ConcreteElement concreteChild2 = (ConcreteElement) choiceElement2Obj;

        Assert.assertEquals(concreteChild1.getElement().getClass(), XsdElement.class);
        Assert.assertEquals(concreteChild2.getElement().getClass(), XsdElement.class);

        Assert.assertEquals("body", ((XsdElement)concreteChild1.getElement()).getName());
        Assert.assertEquals("head", ((XsdElement)concreteChild2.getElement()).getName());
    }

    @Test
    public void testFirstElementAttributes() throws Exception {
        ReferenceBase firstElement = elements.get(0);

        Assert.assertEquals(firstElement.getClass(), ConcreteElement.class);

        ConcreteElement concreteFirstElement = (ConcreteElement) firstElement;

        Assert.assertEquals(concreteFirstElement.getElement().getClass(), XsdElement.class);

        XsdElement htmlElement = (XsdElement) concreteFirstElement.getElement();

        Assert.assertEquals("html", htmlElement.getName());

        XsdComplexType firstElementChild = htmlElement.getComplexType();

        List<ReferenceBase> elementAttributes = firstElementChild.getAttributes();

        Assert.assertEquals(84, elementAttributes.size());
    }
}
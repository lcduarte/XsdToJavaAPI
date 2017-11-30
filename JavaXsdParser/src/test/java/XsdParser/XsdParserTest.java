package XsdParser;

import XsdElements.*;
import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class XsdParserTest {

    private static final List<XsdElement> elements;

    static{
        XsdParser parser = new XsdParser();

        elements = parser.parse("html_5.xsd")
                         .stream()
                         .filter(element -> element instanceof XsdElement)
                         .map(element -> (XsdElement) element)
                         .collect(Collectors.toList());
    }

    @Test
    public void testElementCount() throws Exception {
        Assert.assertEquals(104, elements.size());
    }

    @Test
    public void testFirstElementContents() throws Exception {
        XsdElement htmlElement = elements.get(0);

        Assert.assertEquals("html", htmlElement.getName());

        XsdComplexType firstElementChild = htmlElement.getXsdComplexType();

        Assert.assertEquals(firstElementChild.getXsdChildElement().getClass(), XsdChoice.class);

        XsdChoice complexTypeChild = (XsdChoice) firstElementChild.getXsdChildElement();

        List<XsdElementBase> choiceElements = complexTypeChild.getXsdElements();

        Assert.assertEquals(2, choiceElements.size());

        XsdElementBase child1 = choiceElements.get(0);
        XsdElementBase child2 = choiceElements.get(1);

        Assert.assertEquals(child1.getClass(), XsdElement.class);
        Assert.assertEquals(child2.getClass(), XsdElement.class);

        Assert.assertEquals("body", ((XsdElement)child1).getName());
        Assert.assertEquals("head", ((XsdElement)child2).getName());
    }

    @Test
    public void testFirstElementAttributes() throws Exception {
        XsdElement htmlElement = elements.get(0);

        Assert.assertEquals("html", htmlElement.getName());

        XsdComplexType firstElementChild = htmlElement.getXsdComplexType();

        List<XsdAttribute> elementAttributes = firstElementChild.getXsdAttributes();

        Assert.assertEquals(84, elementAttributes.size());
    }
}
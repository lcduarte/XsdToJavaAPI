package XsdParser;

import XsdElements.*;
import org.junit.Assert;

import java.util.List;

class XsdParserTest {

    private static final List<XsdElement> elements;

    static{
        XsdParser parser = new XsdParser();

        elements = parser.parse("html_5.xsd");
    }

    @org.junit.jupiter.api.Test
    void testElementCount() throws Exception {
        Assert.assertEquals(104, elements.size());//elements.size());
    }

    @org.junit.jupiter.api.Test
    void testFirstElementContents() throws Exception {
        XsdElement firstElement = elements.get(0);

        Assert.assertEquals("html", firstElement.getName());

        XsdComplexType firstElementChild = firstElement.getComplexType();

        Assert.assertEquals(firstElementChild.getChildElement().getClass(), XsdChoice.class);

        XsdChoice complexTypeChild = (XsdChoice) firstElementChild.getChildElement();

        List<XsdElement> choiceElements = complexTypeChild.getElements();

        Assert.assertEquals(2, choiceElements.size());

        XsdElement choiceElement1Obj = choiceElements.get(0);
        XsdElement choiceElement2Obj = choiceElements.get(1);

        Assert.assertEquals("body", choiceElement1Obj.getName());
        Assert.assertEquals("head", choiceElement2Obj.getName());
    }

    @org.junit.jupiter.api.Test
    void testFirstElementAttributes() throws Exception {
        XsdElement firstElement = elements.get(0);

        Assert.assertEquals("html", firstElement.getName());

        XsdComplexType firstElementChild = firstElement.getComplexType();

        List<XsdAttribute> elementAttributes = firstElementChild.getAttributes();

        Assert.assertEquals(84, elementAttributes.size());
    }
}
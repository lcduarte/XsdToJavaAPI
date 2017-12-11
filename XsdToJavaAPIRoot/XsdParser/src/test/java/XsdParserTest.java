import XsdElements.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import XsdParser.*;

public class XsdParserTest {

    private static final String FILE_NAME = XsdParserTest.class.getClassLoader().getResource("html_5.xsd").getPath();
    private static final List<XsdElement> elements;
    private static final XsdParser parser;

    static{
        parser = new XsdParser();

        elements = parser.parse(FILE_NAME)
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

        List<XsdElementBase> choiceElements = complexTypeChild.getXsdElements().collect(Collectors.toList());

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

        List<XsdAttribute> elementAttributes = firstElementChild.getXsdAttributes().collect(Collectors.toList());

        Assert.assertEquals(84, elementAttributes.size());
    }

    @Test
    public void testUnsolvedReferences() throws Exception {
        List<UnsolvedReferenceItem> unsolvedReferenceList = parser.getUnsolvedReferencesForFile(FILE_NAME);

        Assert.assertEquals(1, unsolvedReferenceList.size());

        List<XsdElementBase> parents = unsolvedReferenceList.get(0).getParents();

        Assert.assertEquals(2, parents.size());

        XsdElementBase parent1 = parents.get(0);
        XsdElementBase parent2 = parents.get(1);

        Assert.assertEquals(XsdGroup.class, parent1.getClass());
        Assert.assertEquals(XsdGroup.class, parent2.getClass());

        XsdGroup parent1Group = (XsdGroup) parent1;
        XsdGroup parent2Group = (XsdGroup) parent2;

        Assert.assertEquals("flowContent", parent1Group.getName());
        Assert.assertEquals("phrasingContent", parent2Group.getName());
    }
}
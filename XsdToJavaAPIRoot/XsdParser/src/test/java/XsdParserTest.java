import XsdElements.*;
import XsdElements.XsdRestrictionElements.XsdEnumeration;
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

        List<XsdAbstractElement> choiceElements = complexTypeChild.getXsdElements().collect(Collectors.toList());

        Assert.assertEquals(2, choiceElements.size());

        XsdAbstractElement child1 = choiceElements.get(0);
        XsdAbstractElement child2 = choiceElements.get(1);

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

        Assert.assertEquals(4, unsolvedReferenceList.size());

        List<XsdAbstractElement> parents = unsolvedReferenceList.get(0).getParents();

        Assert.assertEquals(4, parents.size());

        XsdAbstractElement parent1 = parents.get(0);
        XsdAbstractElement parent2 = parents.get(1);
        XsdAbstractElement parent3 = parents.get(2);
        XsdAbstractElement parent4 = parents.get(3);

        Assert.assertEquals(XsdAttribute.class, parent1.getClass());
        Assert.assertEquals(XsdAttribute.class, parent2.getClass());
        Assert.assertEquals(XsdAttribute.class, parent3.getClass());
        Assert.assertEquals(XsdAttribute.class, parent4.getClass());

        XsdAttribute parent1Attr = (XsdAttribute) parent1;
        XsdAttribute parent2Attr = (XsdAttribute) parent2;
        XsdAttribute parent3Attr = (XsdAttribute) parent3;
        XsdAttribute parent4Attr = (XsdAttribute) parent4;

        Assert.assertEquals("lang", parent1Attr.getName());
        Assert.assertEquals("hreflang", parent2Attr.getName());
        Assert.assertEquals("hreflang", parent3Attr.getName());
        Assert.assertEquals("hreflang", parent4Attr.getName());
    }

    @Test
    public void testSimpleTypes() throws Exception {
        XsdElement htmlElement = elements.get(5);

        Assert.assertEquals("meta", htmlElement.getName());

        XsdComplexType metaChild = htmlElement.getXsdComplexType();

        XsdAttribute attribute = metaChild.getXsdAttributes().findFirst().get();

        Assert.assertEquals("http-equiv", attribute.getName());

        Assert.assertEquals(true, attribute.getXsdSimpleType() != null);

        XsdSimpleType simpleType = attribute.getXsdSimpleType();

        Assert.assertNull(simpleType.getRestriction());
        Assert.assertNull(simpleType.getList());
        Assert.assertNotNull(simpleType.getUnion());

        XsdUnion union = simpleType.getUnion();

        Assert.assertEquals(2, union.getUnionElements().size());

        XsdSimpleType innerSimpleType1 = union.getUnionElements().get(0);

        Assert.assertNotNull(innerSimpleType1.getRestriction());
        Assert.assertNull(innerSimpleType1.getList());
        Assert.assertNull(innerSimpleType1.getUnion());

        XsdRestriction restriction = innerSimpleType1.getRestriction();

        List<XsdEnumeration> enumeration = restriction.getEnumeration();

        Assert.assertEquals(4, enumeration.size());

        Assert.assertEquals("content-language", enumeration.get(0).getValue());
        Assert.assertEquals("content-type", enumeration.get(1).getValue());
        Assert.assertEquals("default-style", enumeration.get(2).getValue());
        Assert.assertEquals("refresh", enumeration.get(3).getValue());

        Assert.assertNull(restriction.getFractionDigits());
        Assert.assertNull(restriction.getLength());
        Assert.assertNull(restriction.getMaxExclusive());
        Assert.assertNull(restriction.getMaxInclusive());
        Assert.assertNull(restriction.getMaxLength());
        Assert.assertNull(restriction.getMinExclusive());
        Assert.assertNull(restriction.getMinInclusive());
        Assert.assertNull(restriction.getMinLength());
        Assert.assertNull(restriction.getPattern());
        Assert.assertNull(restriction.getTotalDigits());
        Assert.assertNull(restriction.getWhiteSpace());
    }
}
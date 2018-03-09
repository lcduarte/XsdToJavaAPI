package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.UnsolvedReferenceItem;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdComplexContent;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AndroidParseTest {

    private static final String ANDROID_FILE_NAME = AndroidParseTest.class.getClassLoader().getResource("android.xsd").getPath();
    private static final List<XsdElement> elements;
    private static final XsdParser parser;

    static {
        parser = new XsdParser();

        elements = parser.parse(ANDROID_FILE_NAME)
                .filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());
    }

    @Test
    public void testHierarchy() {
        Optional<XsdElement> relativeLayoutOptional = elements.stream().filter(element -> element.getName().equals("RelativeLayout")).findFirst();

        Assert.assertTrue(relativeLayoutOptional.isPresent());

        XsdElement relativeLayout = relativeLayoutOptional.get();

        XsdComplexType relativeLayoutComplexType = relativeLayout.getXsdComplexType();

        Assert.assertNotNull(relativeLayoutComplexType);

        XsdComplexContent relativeLayoutComplexContent = relativeLayoutComplexType.getComplexContent();

        Assert.assertNotNull(relativeLayoutComplexContent);

        XsdExtension relativeLayoutExtension = relativeLayoutComplexContent.getXsdExtension();

        XsdElement viewGroup = relativeLayoutExtension.getBase();

        Assert.assertNotNull(viewGroup);
        Assert.assertEquals("ViewGroup", viewGroup.getName());

        XsdComplexType viewGroupComplexType = viewGroup.getXsdComplexType();

        Assert.assertNotNull(viewGroupComplexType);

        XsdComplexContent viewGroupComplexContent = viewGroupComplexType.getComplexContent();

        Assert.assertNotNull(viewGroupComplexContent);

        XsdExtension viewGroupExtension = viewGroupComplexContent.getXsdExtension();

        XsdElement view = viewGroupExtension.getBase();

        Assert.assertNotNull(view);
        Assert.assertEquals("View", view.getName());
    }

    @Test
    public void testDebug() {
        Optional<XsdElement> relativeLayoutOptional = elements.stream().filter(element -> element.getName().equals("LinearLayout")).findFirst();

        XsdElement linearLayout = relativeLayoutOptional.get();
    }

    @Test
    public void testUnsolved() {
        List<UnsolvedReferenceItem> var = parser.getUnsolvedReferencesForFile(ANDROID_FILE_NAME);


    }
}
import XsdClassGenerator.XsdClassGenerator;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.XsdElementBase;
import XsdParser.XsdParser;

import java.util.List;

public class XsdMain {

    public static void main(String argv[]) {
        XsdParser xsdParser = new XsdParser();
        XsdClassGenerator classGenerator = new XsdClassGenerator();

        List<XsdElementBase> x = xsdParser.parse("html_5.xsd");

        List<UnsolvedReference> d = xsdParser.getUnsolvedReferencesForFile("html_5.xsd");
        classGenerator.generateClassFromElements(x);
    }
}
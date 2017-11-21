import XsdClassGenerator.XsdClassGenerator;
import XsdElements.XsdElement;
import XsdParser.XsdParser;

import java.util.List;

public class XsdMain {

    public static void main(String argv[]) {
        XsdParser xsdParser = new XsdParser();
        XsdClassGenerator classGenerator = new XsdClassGenerator();

        classGenerator.generateClassFromElements(xsdParser.parse("html_5.xsd"));

        System.out.println("Done");
    }
}
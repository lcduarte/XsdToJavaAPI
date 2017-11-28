import XsdClassGenerator.XsdClassGenerator;
import XsdParser.XsdParser;

public class XsdMain {

    public static void main(String argv[]) {
        XsdParser xsdParser = new XsdParser();
        XsdClassGenerator classGenerator = new XsdClassGenerator();

        classGenerator.generateClassFromElements(xsdParser.parse("html_5.xsd"));
    }
}
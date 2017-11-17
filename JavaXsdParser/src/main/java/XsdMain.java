import XsdElements.XsdElement;
import XsdParser.XsdParser;

import java.util.List;

public class XsdMain {

    public static void main(String argv[]) {
        XsdParser xsdParser = new XsdParser();
        List<XsdElement> elements = xsdParser.parse("html_5.xsd");

        System.out.println("Done");
    }
}
import XsdClassGenerator.XsdClassGenerator;
import XsdElements.XsdElement;
import XsdElements.XsdElementBase;
import XsdParser.XsdParser;

import java.util.HashMap;
import java.util.List;

public class XsdMain {

    public static void main(String argv[]) {
        XsdParser xsdParser = new XsdParser();
        XsdClassGenerator classGenerator = new XsdClassGenerator();

        List<XsdElementBase> parsedObjects = xsdParser.parse("html_5.xsd");

        //HashMap<String, String> textElementMap = new HashMap<>();
        //textElementMap.put("name", "text");

        //parsedObjects.add(new XsdElement(textElementMap));

        classGenerator.generateClassFromElements(parsedObjects);
    }
}
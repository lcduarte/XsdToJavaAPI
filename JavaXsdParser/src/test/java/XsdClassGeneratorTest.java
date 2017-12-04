import XsdClassGenerator.*;
import XsdElements.XsdElement;
import XsdElements.XsdElementBase;
import org.junit.Test;
import XsdParser.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("Duplicates")
public class XsdClassGeneratorTest {

    static{
        cleanDirectory();

        XsdParser xsdParser = new XsdParser();
        XsdClassGenerator classGenerator = new XsdClassGenerator();

        List<XsdElementBase> parsedObjects = xsdParser.parse("html_5.xsd");

        HashMap<String, String> textElementMap = new HashMap<>();
        textElementMap.put("name", "text");

        parsedObjects.add(new XsdElement(textElementMap));

        classGenerator.generateClassFromElements(parsedObjects);
    }

    private static void cleanDirectory() {
        File dir = new File(XsdClassGeneratorUtils.getDestinationDirectory());

        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        File generatedObjectsFolder = new File(XsdClassGeneratorUtils.getDestinationDirectory());
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{
                        new URL("file://" + XsdClassGeneratorUtils.getDestinationDirectory()) });

        for (File generatedFile : generatedFiles) {
            if (generatedFile.getName().endsWith(".class")){
                String absolutePath = generatedFile.getAbsolutePath();

                String className = absolutePath.substring(absolutePath.lastIndexOf('\\') + 1, absolutePath.indexOf(".class"));
                System.out.println(className);

                ucl.loadClass( "XsdClassGenerator.ParsedObjects." + className);
            }
        }
    }
}
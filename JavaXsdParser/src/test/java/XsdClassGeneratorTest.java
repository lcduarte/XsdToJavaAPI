import XsdClassGenerator.XsdClassGenerator;
import XsdClassGenerator.XsdClassGeneratorUtils;
import XsdElements.XsdElement;
import XsdElements.XsdElementBase;
import XsdParser.XsdParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
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

        //HashMap<String, String> textElementMap = new HashMap<>();
        //textElementMap.put("name", "text");

        //parsedObjects.add(new XsdElement(textElementMap));

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
                //System.out.println(className);

                ucl.loadClass( "XsdClassGenerator.ParsedObjects." + className);
            }
        }
    }

    @Test
    public void testSelf() throws Exception {
        File generatedObjectsFolder = new File(XsdClassGeneratorUtils.getDestinationDirectory());
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{
                        new URL("file://" + XsdClassGeneratorUtils.getDestinationDirectory()) });

        Class var = ucl.loadClass("XsdClassGenerator.ParsedObjects.Var");
        Object varInstance1 = var.newInstance();

        Method addVarMethod = var.getMethod("var", String.class);
        Method addVarMethodWithId = var.getMethod("var", String.class, String.class);

        addVarMethod.invoke(varInstance1, "varContent");

        //Assert.assertEquals(1, varInstance1.childs.size());

        Object varInstance2 = var.newInstance();

        Object returningVar = addVarMethodWithId.invoke(varInstance2, "varContent", "varId");

        //Assert.assertEquals(varInstance2, returningVar);
        //Assert.assertEquals("varId", varInstance2.childs.get(0).id());
    }

    @Test
    public void testAttributeGroups() throws Exception {
        File generatedObjectsFolder = new File(XsdClassGeneratorUtils.getDestinationDirectory());
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{
                        new URL("file://" + XsdClassGeneratorUtils.getDestinationDirectory()) });

        Class c = ucl.loadClass("XsdClassGenerator.ParsedObjects.ICoreAttributeGroup");

        c.getDeclaredMethods();
    }
}
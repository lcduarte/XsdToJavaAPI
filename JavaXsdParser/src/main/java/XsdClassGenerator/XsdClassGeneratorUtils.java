package XsdClassGenerator;

import XsdElements.XsdElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class XsdClassGeneratorUtils {

    private static final String PACKAGE = "XsdClassGenerator/ParsedObjects/";
    private static final String INTERFACE_PREFIX = "I";

    static String getInterfaceName(String groupName) {
        return INTERFACE_PREFIX + XsdClassGeneratorUtils.toCamelCase(groupName);
    }

    static String toCamelCase(String name){
        if (name.length() == 1){
            return name.toUpperCase();
        }

        String firstLetter = name.substring(0, 1).toUpperCase();
        return firstLetter + name.substring(1);
    }

    private static String getDestinationDirectory(){
        URL resource = XsdClassGenerator.class.getClassLoader().getResource("");

        if (resource != null){
            return resource.getPath() + "\\XsdClassGenerator\\ParsedObjects\\";
        }

        throw new RuntimeException("Target folder not found.");
    }

    private static String getFinalPathPart(String className){
        return getDestinationDirectory() + className + ".class";
    }

    static String getFullClassTypeName(String className){
        return PACKAGE + className;
    }

    static String getFullClassName(String className){
        return "L" + PACKAGE + className + ";";
    }

    static void createGeneratedFilesDirectory() {
        File folder = new File(XsdClassGeneratorUtils.getDestinationDirectory());

        if (!folder.exists()){
            folder.mkdirs();
        }
    }

    static void writeClassToFile(String className, byte[] constructedClass){
        try {
            FileOutputStream os = new FileOutputStream(new File(getFinalPathPart(className)));
            os.write(constructedClass);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

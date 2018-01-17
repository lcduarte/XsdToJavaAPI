package XsdAsm;

import XsdElements.*;
import XsdElements.XsdRestrictionElements.*;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmAttributes.*;
import static XsdAsm.XsdSupportingStructure.*;
import static org.objectweb.asm.Opcodes.*;

public class XsdAsmUtils {

    @SuppressWarnings("FieldCanBeLocal")
    private static String PACKAGE_BASE = "XsdToJavaAPI/";
    private static final String INTERFACE_PREFIX = "I";
    private static final HashMap<String, String> xsdTypesToJava;
    private static final HashMap<String, String> xsdFullTypesToJava;

    static {
        xsdTypesToJava = new HashMap<>();
        xsdFullTypesToJava = new HashMap<>();

        xsdTypesToJava.put("xsd:anyURI", "String");
        xsdTypesToJava.put("xsd:boolean", "Boolean");
        //xsdTypesToJava.put("xsd:base64Binary", "[B");
        //xsdTypesToJava.put("xsd:hexBinary", "[B");
        xsdTypesToJava.put("xsd:date", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:dateTime", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:time", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:duration", "Duration");
        xsdTypesToJava.put("xsd:dayTimeDuration", "Duration");
        xsdTypesToJava.put("xsd:yearMonthDuration", "Duration");
        xsdTypesToJava.put("xsd:gDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gMonthDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gYear", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gYearMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:decimal", "BigDecimal");
        xsdTypesToJava.put("xsd:integer", "BigInteger");
        xsdTypesToJava.put("xsd:nonPositiveInteger", "BigInteger");
        xsdTypesToJava.put("xsd:negativeInteger", "BigInteger");
        xsdTypesToJava.put("xsd:long", "Long");
        xsdTypesToJava.put("xsd:int", "Integer");
        xsdTypesToJava.put("xsd:short", "Short");
        xsdTypesToJava.put("xsd:byte", "Byte");
        xsdTypesToJava.put("xsd:nonNegativeInteger", "BigInteger");
        xsdTypesToJava.put("xsd:unsignedLong", "BigInteger");
        xsdTypesToJava.put("xsd:unsignedInt", "Long");
        xsdTypesToJava.put("xsd:unsignedShort", "Integer");
        xsdTypesToJava.put("xsd:unsignedByte", "Short");
        xsdTypesToJava.put("xsd:positiveInteger", "BigInteger");
        xsdTypesToJava.put("xsd:double", "Double");
        xsdTypesToJava.put("xsd:float", "Float");
        xsdTypesToJava.put("xsd:QName", "QName");
        xsdTypesToJava.put("xsd:NOTATION", "QName");
        xsdTypesToJava.put("xsd:string", "java.lang.String");
        xsdTypesToJava.put("xsd:normalizedString", "String");
        xsdTypesToJava.put("xsd:token", "String");
        xsdTypesToJava.put("xsd:language", "String");
        xsdTypesToJava.put("xsd:NMTOKEN", "java.lang.String");
        xsdTypesToJava.put("xsd:Name", "String");
        xsdTypesToJava.put("xsd:NCName", "String");
        xsdTypesToJava.put("xsd:ID", "String");
        xsdTypesToJava.put("xsd:IDREF", "String");
        xsdTypesToJava.put("xsd:ENTITY", "String");
        xsdTypesToJava.put("xsd:untypedAtomic", "String");

        xsdFullTypesToJava.put("xsd:anyURI","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:boolean","Ljava/lang/Boolean;");
        xsdFullTypesToJava.put("xsd:date","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:dateTime","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:time","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:duration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:dayTimeDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:yearMonthDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:gDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gMonthDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gYear","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gYearMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:decimal","Ljava/math/BigDecimal;");
        xsdFullTypesToJava.put("xsd:integer","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:nonPositiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:negativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:long","Ljava/lang/Long;");
        xsdFullTypesToJava.put("xsd:int","Ljava/lang/Integer;");
        xsdFullTypesToJava.put("xsd:short","Ljava/lang/Short;");
        xsdFullTypesToJava.put("xsd:byte","Ljava/lang/Byte;");
        xsdFullTypesToJava.put("xsd:nonNegativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:unsignedLong","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:unsignedInt", "java/lang/Long;");
        xsdFullTypesToJava.put("xsd:unsignedShort", "java/lang/Integer;");
        xsdFullTypesToJava.put("xsd:unsignedByte", "java/lang/Short;");
        xsdFullTypesToJava.put("xsd:positiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:double","Ljava/lang/Double;");
        xsdFullTypesToJava.put("xsd:float","Ljava/lang/Float;");
        xsdFullTypesToJava.put("xsd:QName","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xsd:NOTATION","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xsd:string","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:normalizedString","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:token","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:language","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:NMTOKEN","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:Name","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:NCName","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:ID","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:IDREF","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:ENTITY","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:untypedAtomic","Ljava/lang/String;");
    }

    /**
     * @param groupName A group/interface name.
     * @return An interface-like name, e.g. flowContent -> IFlowContent
     */
    static String getInterfaceName(String groupName) {
        return INTERFACE_PREFIX + toCamelCase(groupName);
    }

    static String toCamelCase(String name){
        if (name.length() == 1){
            return name.toUpperCase();
        }

        String firstLetter = name.substring(0, 1).toUpperCase();
        return firstLetter + name.substring(1);
    }

    public static String getPackage(String apiName){
        return PACKAGE_BASE + apiName + "/";
    }

    /**
     * @return The path to the destination folder of all the generated classes.
     */
    public static String getDestinationDirectory(String apiName){
        URL resource = XsdAsm.class.getClassLoader().getResource("");

        if (resource != null){
            return resource.getPath() + "/" + getPackage(apiName);
        }

        throw new RuntimeException("Target folder not found.");
    }

    /**
     * @param className The class name.
     * @return The complete file path to the given class name.
     */
    private static String getFinalPathPart(String className, String apiName){
        return getDestinationDirectory(apiName) + className + ".class";
    }

    /**
     * @param className The class name.
     * @return The full type of the class, e.g. Html -> XsdAsm/ParsedObjects/Html
     */
    static String getFullClassTypeName(String className, String apiName){
        return getPackage(apiName) + className;
    }

    /**
     * @param className The class name.
     * @return The full type descriptor of the class, e.g. Html -> LXsdClassGenerator/ParsedObjects/Html;
     */
    static String getFullClassTypeNameDesc(String className, String apiName){
        return "L" + getPackage(apiName) + className + ";";
    }

    /**
     * Creates the destination directory of the generated files, if not exists.
     */
    static void createGeneratedFilesDirectory(String apiName) {
        File folder = new File(getDestinationDirectory(apiName));

        if (!folder.exists()){
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
    }

    /**
     * Writes a given class to a .class file.
     * @param className The class name, needed to name the file.
     * @param classWriter The classWriter, which contains all the class information.
     */
    static void writeClassToFile(String className, ClassWriter classWriter, String apiName){
        classWriter.visitEnd();

        byte[] constructedClass = classWriter.toByteArray();

        try {
            FileOutputStream os = new FileOutputStream(new File(getFinalPathPart(className, apiName)));
            os.write(constructedClass);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean isInterfaceMethod(String returnType) {
        return returnType.equals(IELEMENT_TYPE_DESC);
    }

    static String getJavaType(XsdAttribute attribute, List<XsdRestriction> restrictions) {
        return getJavaType(attribute, restrictions, xsdTypesToJava, "Object");
    }

    static String getFullJavaType(XsdAttribute attribute) {
        return getJavaType(attribute, getAttributeRestrictions(attribute), xsdFullTypesToJava, JAVA_OBJECT_DESC);
    }

    static String getFullJavaType(String itemType) {
        return xsdFullTypesToJava.getOrDefault(itemType, JAVA_OBJECT_DESC);
    }

    private static String getJavaType(XsdAttribute attribute, List<XsdRestriction> restrictions, HashMap<String, String> xsdTypes, String defaultType){
        String javaType = xsdTypes.getOrDefault(attribute.getType(), null);

        if (javaType == null){
            if (restrictions.size() != 0){
                return xsdTypes.getOrDefault(restrictions.get(0).getBase(), defaultType);
            }

            return defaultType;
        }

        return javaType;
    }

    static String getEnumElementName(XsdEnumeration enumElem) {
        return enumElem.getValue().toUpperCase().replaceAll("[^a-zA-Z0-9]", "_");
    }

    static List<XsdRestriction> getAttributeRestrictions(XsdAttribute attribute) {
        try {
            return attribute.getAllRestrictions();
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage() + " at attribute with name = " + attribute.getName());
        }
    }

    /**
     * Generates the required methods for adding a given xsdAttribute and creates the
     * respective class, if needed.
     * @param classWriter The class writer to write the methods.
     * @param elementAttribute The attribute element.
     * @param apiName The api this class will belong.
     */
    static void generateMethodsAndCreateAttribute(List<String> createdAttributes, ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String apiName) {
        generateMethodsForAttribute(classWriter, elementAttribute, returnType, apiName);

        if (!createdAttributes.contains(elementAttribute.getName())){
            generateAttribute(elementAttribute, apiName);

            createdAttributes.add(elementAttribute.getName());
        }
    }

    /**
     * Returns all the concrete children of a given element. With the separation made between
     * XsdGroups children and the remaining children this method is able to return only the
     * children that are not shared in any interface.
     * @param element The element from which the children will be obtained.
     * @return The children that are exclusive to the current element.
     */
    static Stream<XsdElement> getOwnChildren(XsdElement element) {
        if (element.getXsdComplexType() != null){
            XsdAbstractElement childElement = element.getXsdComplexType().getXsdChildElement();

            if (childElement != null) {
                Map<String, XsdElement> mappedElements = new HashMap<>();

                childElement
                        .getXsdElements()
                        .filter(referenceBase -> !(referenceBase.getParent() instanceof XsdGroup))
                        .map(referenceBase -> (XsdElement) referenceBase)
                        .forEach(elementObj -> mappedElements.put(elementObj.getName(), elementObj));

                return mappedElements.values().stream();
            }
        }

        return Stream.empty();
    }

    /**
     * Obtains the attributes which are specific to the given element.
     * @param element The element containing the attributes.
     * @return A list of attributes that are exclusive to the element.
     */
    static Stream<XsdAttribute> getOwnAttributes(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            return complexType.getXsdAttributes()
                    .filter(attribute -> attribute.getParent().equals(complexType));
        }

        return Stream.empty();
    }

    /**
     * Obtains the signature for a class given the interface names.
     * @param interfaces The implemented interfaces.
     * @param className The class name.
     * @param apiName The api this class will belong.
     * @return The signature of the class.
     */
    static String getClassSignature(String[] interfaces, String className, String apiName) {
        StringBuilder signature = new StringBuilder("L" + ABSTRACT_ELEMENT_TYPE + "<" + getFullClassTypeNameDesc(className, apiName) + ">;");

        for (String anInterface : interfaces) {
            signature.append("L")
                    .append(getFullClassTypeName(anInterface, apiName))
                    .append("<")
                    .append(getFullClassTypeNameDesc(className, apiName))
                    .append(">;");
        }

        return signature.toString();
    }

    /**
     * Generates a default constructor.
     * @param classWriter The class writer from the class where the constructors will be added.
     * @param constructorType The modifiers for the constructor.
     */
    static void generateConstructor(ClassWriter classWriter, String baseClass, int constructorType) {
        MethodVisitor defaultConstructor = classWriter.visitMethod(constructorType, CONSTRUCTOR, "()V",null,null);

        defaultConstructor.visitCode();
        defaultConstructor.visitVarInsn(ALOAD, 0);
        defaultConstructor.visitMethodInsn(INVOKESPECIAL, baseClass, CONSTRUCTOR, "()V", false);
        defaultConstructor.visitInsn(RETURN);
        defaultConstructor.visitMaxs(1, 1);

        defaultConstructor.visitEnd();
    }

    /**
     * Generates an empty class.
     * @param className The classes name.
     * @param superName The super object, which the class extends from.
     * @param interfaces The name of the interfaces which this class implements.
     * @param classModifiers The modifiers to the class.
     * @return A class writer that will be used to write the remaining information of the class.
     */
    static ClassWriter generateClass(String className, String superName, String[] interfaces, String signature, int classModifiers, String apiName) {
        ClassWriter classWriter = new ClassWriter(0);

        if (interfaces != null){
            for (int i = 0; i < interfaces.length; i++) {
                interfaces[i] = getFullClassTypeName(interfaces[i], apiName);
            }
        }

        classWriter.visit(V1_8, classModifiers, getFullClassTypeName(className, apiName), signature, superName, interfaces);

        return classWriter;
    }

}


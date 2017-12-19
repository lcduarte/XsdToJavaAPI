package XsdClassGenerator;

import XsdElements.XsdAttribute;
import XsdElements.XsdElement;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static XsdClassGenerator.XsdClassGenerator.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class XsdClassGeneratorUtils {

    public static String PACKAGE_BASE = "XsdToJavaAPI/";
    private static final String INTERFACE_PREFIX = "I";
    private static final HashMap<String, String> xsdTypesToJava;

    static {
        xsdTypesToJava = new HashMap<>();

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
        xsdTypesToJava.put("xsd:string", "String");
        xsdTypesToJava.put("xsd:normalizedString", "String");
        xsdTypesToJava.put("xsd:token", "String");
        xsdTypesToJava.put("xsd:language", "String");
        xsdTypesToJava.put("xsd:NMTOKEN", "String");
        xsdTypesToJava.put("xsd:Name", "String");
        xsdTypesToJava.put("xsd:NCName", "String");
        xsdTypesToJava.put("xsd:ID", "String");
        xsdTypesToJava.put("xsd:IDREF", "String");
        xsdTypesToJava.put("xsd:ENTITY", "String");
        xsdTypesToJava.put("xsd:untypedAtomic", "String");
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
        URL resource = XsdClassGenerator.class.getClassLoader().getResource("");

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
     * @return The full type of the class, e.g. Html -> XsdClassGenerator/ParsedObjects/Html
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

    /**
     * Creates the base infrastructure, based in the main three classes:
     * IElement - An interface containing the base operations of all elements.
     * IAttribute - An interface containing the base operations of all attributes.
     * AbstractElement - An abstract class from where all the elements will derive. It implements IElement.
     * Text - A concrete attribute with a different implementation that the other generated attributes.
     */
    static void createSupportingInfrastructure(String apiName) {
        TEXT_TYPE = getFullClassTypeName(TEXT_CLASS, apiName);
        TEXT_TYPE_DESC = getFullClassTypeNameDesc(TEXT_CLASS, apiName);
        ABSTRACT_ELEMENT_TYPE = getFullClassTypeName(ABSTRACT_ELEMENT, apiName);
        ABSTRACT_ELEMENT_TYPE_DESC = getFullClassTypeNameDesc(ABSTRACT_ELEMENT, apiName);
        ABSTRACT_ATTRIBUTE_TYPE = getFullClassTypeName(ABSTRACT_ATTRIBUTE, apiName);
        ABSTRACT_ATTRIBUTE_TYPE_DESC = getFullClassTypeNameDesc(ABSTRACT_ATTRIBUTE, apiName);
        IELEMENT_TYPE = getFullClassTypeName(IELEMENT, apiName);
        IELEMENT_TYPE_DESC = getFullClassTypeNameDesc(IELEMENT, apiName);
        IATTRIBUTE_TYPE = getFullClassTypeName(IATTRIBUTE, apiName);
        IATTRIBUTE_TYPE_DESC = getFullClassTypeNameDesc(IATTRIBUTE, apiName);
        ITEXT_TYPE = getFullClassTypeName(ITEXT, apiName);
        ITEXT_TYPE_DESC = getFullClassTypeNameDesc(ITEXT, apiName);

        createAbstractElement(apiName);
        createAbstractAttribute(apiName);
        createAttributeInterface(apiName);
        createElementInterface(apiName);
        createTextElement(apiName);
        createTextGroupInterface(apiName);
    }

    private static void createAbstractAttribute(String apiName) {
        ClassWriter classWriter = generateClass(ABSTRACT_ATTRIBUTE, JAVA_OBJECT, new String[] { IATTRIBUTE }, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC + "L" + IATTRIBUTE_TYPE + "<TT;>;", ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "value", JAVA_OBJECT_DESC, "TT;", null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(0, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", "(TT;)V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ATTRIBUTE_TYPE, "value", JAVA_OBJECT_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_OBJECT_DESC, "()TT;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ATTRIBUTE_TYPE, "value", JAVA_OBJECT_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(ABSTRACT_ATTRIBUTE, classWriter, apiName);
    }

    private static void createTextGroupInterface(String apiName) {
        ClassWriter classWriter = generateClass(ITEXT, JAVA_OBJECT, new String[] { IELEMENT }, "<T::L" + IELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, TEXT_CLASS.toLowerCase(), "(" + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + ")TT;", null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, TEXT_TYPE);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, TEXT_TYPE, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, ITEXT_TYPE, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        writeClassToFile(ITEXT, classWriter, apiName);
    }

    /**
     * Generates the AbstractElement class with all the implementations.
     */
    private static void createAbstractElement(String apiName){
        ClassWriter classWriter = generateClass(ABSTRACT_ELEMENT, JAVA_OBJECT, new String[] { IELEMENT }, "<T::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;",ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, apiName);
        FieldVisitor fVisitor;
        MethodVisitor mVisitor;

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        fVisitor = classWriter.visitField(ACC_PROTECTED, "children", JAVA_LIST_DESC , "L" + JAVA_LIST + "<" + IELEMENT_TYPE_DESC + ">;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "attrs", JAVA_LIST_DESC, "L" + JAVA_LIST + "<" + IATTRIBUTE_TYPE_DESC + ">;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "id", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "children", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", JAVA_LIST_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "children", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(" + JAVA_OBJECT_DESC + ")Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(" + JAVA_OBJECT_DESC + ")Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "id", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "child", "(" + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "<R::" + IELEMENT_TYPE_DESC + ">(" + JAVA_STRING_DESC + ")TR;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE_DESC, "children", JAVA_LIST_DESC);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "stream", "()Ljava/util/stream/Stream;", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("test", "(" + JAVA_STRING_DESC + ")Ljava/util/function/Predicate;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")Z"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$0", "(" + JAVA_STRING_DESC + IELEMENT_TYPE_DESC + ")Z", false), Type.getType("(" + IELEMENT_TYPE_DESC + ")Z"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$1", "(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, false), Type.getType("(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "findFirst", "()Ljava/util/Optional;", true);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "isPresent", "()Z", false);
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "get", "()" + JAVA_OBJECT_DESC, false);
        mVisitor.visitTypeInsn(CHECKCAST, IELEMENT_TYPE);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/util/Optional"}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "children", "Ljava/util/List;");
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "stream", "()Ljava/util/stream/Stream;", true);
        mVisitor.visitInvokeDynamicInsn("test", "()Ljava/util/function/Predicate;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Z"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$2", "(" + IELEMENT_TYPE_DESC + ")Z", false), Type.getType("(" + IELEMENT_TYPE_DESC + ")Z"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$3", "(" + IELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC, false), Type.getType("(" + IELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("test", "(Ljava/lang/String;)Ljava/util/function/Predicate;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Z"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$4", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")Z", false), Type.getType("(" + ABSTRACT_ELEMENT_TYPE_DESC + ")Z"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("apply", "(Ljava/lang/String;)Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$5", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, false), Type.getType("(" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "findFirst", "()Ljava/util/Optional;", true);
        mVisitor.visitInsn(ACONST_NULL);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "orElse", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        mVisitor.visitTypeInsn(CHECKCAST, IELEMENT_TYPE);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$5", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "child", "(Ljava/lang/String;)" + IELEMENT_TYPE_DESC, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$4", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "child", "(Ljava/lang/String;)" + IELEMENT_TYPE_DESC, false);
        Label l1 = new Label();
        mVisitor.visitJumpInsn(IFNULL, l1);
        mVisitor.visitInsn(ICONST_1);
        Label l2 = new Label();
        mVisitor.visitJumpInsn(GOTO, l2);
        mVisitor.visitLabel(l1);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitLabel(l2);
        mVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$3", "(" + IELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(CHECKCAST, ABSTRACT_ELEMENT_TYPE);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$2", "(" + IELEMENT_TYPE_DESC + ")Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(INSTANCEOF, ABSTRACT_ELEMENT_TYPE);
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$1", "(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$0", "(Ljava/lang/String;" + IELEMENT_TYPE_DESC + ")Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "id", "()Ljava/lang/String;", true);
        Label l3 = new Label();
        mVisitor.visitJumpInsn(IFNULL, l3);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "id", "()Ljava/lang/String;", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        mVisitor.visitJumpInsn(IFEQ, l3);
        mVisitor.visitInsn(ICONST_1);
        Label l4 = new Label();
        mVisitor.visitJumpInsn(GOTO, l4);
        mVisitor.visitLabel(l3);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitLabel(l4);
        mVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        writeClassToFile(ABSTRACT_ELEMENT, classWriter, apiName);
    }

    /**
     * Generates the IAttribute interface.
     * @param apiName The api this class will belong.
     */
    private static void createAttributeInterface(String apiName){
        ClassWriter classWriter = generateClass(IATTRIBUTE, JAVA_OBJECT, null, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getValue", "()" + JAVA_OBJECT_DESC, "()TT;", null);
        mVisitor.visitEnd();

        writeClassToFile(IATTRIBUTE, classWriter, apiName);
    }

    /**
     * Generates the IElement interface.
     * @param apiName The api this class will belong.
     */
    private static void createElementInterface(String apiName){
        ClassWriter classWriter = generateClass(IELEMENT, JAVA_OBJECT, null, "<T::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "addAttr", "("+ IATTRIBUTE_TYPE_DESC + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "self", "()" + IELEMENT_TYPE_DESC, "()TT;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "id", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();

        writeClassToFile(IELEMENT, classWriter, apiName);
    }

    /**
     * Creates the Text class.
     * @param apiName The api this class will belong.
     */
    private static void createTextElement(String apiName) {
        ClassWriter classWriter = generateClass(TEXT_CLASS, ABSTRACT_ELEMENT_TYPE, null,  "L" + ABSTRACT_ELEMENT_TYPE + "<" + TEXT_TYPE_DESC + ">;" + IATTRIBUTE_TYPE_DESC,ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE + ACC_FINAL, "text", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, TEXT_TYPE, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, TEXT_TYPE, "text", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(0, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(0, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + TEXT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getText", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, TEXT_TYPE, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, TEXT_TYPE, "self", "()" + TEXT_TYPE_DESC, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(TEXT_CLASS, classWriter, apiName);
    }

    /**
     * Creates some class specific methods that all implementations of AbstractElement should have, which are:
     * A constructor with a String parameter, which is it will create a Text attribute in the created element.
     * A constructor with two String parameters, the first being the value of the Text attribute, and the second being a value for its id.
     * An implementation of the self method, which should return this.
     * @param classWriter The class writer on which should be written the methods.
     * @param className The class name.
     */
    static void generateClassSpecificMethods(ClassWriter classWriter, String className, String apiName) {
        String classType = getFullClassTypeName(className, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(className, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, TEXT_TYPE);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, TEXT_TYPE, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + classTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "self", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param child The child of the element which generated the class. Their name represents a method.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String returnType,  String apiName) {
        String childCamelName = toCamelCase(child.getName());
        String childType = getFullClassTypeName(childCamelName, apiName);
        String childTypeDesc = getFullClassTypeNameDesc(childCamelName, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "()" + childTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ASTORE, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();


        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + ")" + returnType, "(" + JAVA_STRING_DESC + ")" + returnType, null);
        }

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();


        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + returnType, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + returnType, null);
        }

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 3);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + returnType, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 4);
        mVisitor.visitEnd();
    }

    /**
     * Generates a method to add a given attribute.
     * @param classWriter The class where the fields will be added.
     * @param elementAttribute The attribute containing the information to create the method. (Only String fields are being supported)
     */
    @SuppressWarnings("DanglingJavadoc")
    static void generateMethodsForAttribute(ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String apiName) {
        String camelCaseName = ATTRIBUTE_PREFIX + toCamelCase(elementAttribute.getName()).replaceAll("\\W+", "");
        String attributeClassTypeDesc = getFullClassTypeNameDesc(camelCaseName, apiName);
        MethodVisitor mVisitor;

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, "add" + camelCaseName, "(" + attributeClassTypeDesc + ")" + returnType, "(" + attributeClassTypeDesc + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, "add" + camelCaseName, "(" + attributeClassTypeDesc + ")" + returnType, "(" + attributeClassTypeDesc + ")" + returnType, null);
        }

        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        /**
         * The cast to AbstractElement is needed while writing bytecode, even though it's not needed in regular written code.
         */

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitTypeInsn(CHECKCAST, IELEMENT_TYPE);
        }

        mVisitor.visitVarInsn(ALOAD, 1);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE_DESC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE_DESC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + returnType, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }

    /**
     * Creates a class which represents an attribute.
     * @param attribute The XsdAttribute type that contains the required information.
     * @param apiName The api this class will belong.
     */
    static void generateAttribute(XsdAttribute attribute, String apiName){
        //https://www.ibm.com/support/knowledgecenter/en/SSAW57_8.5.5/com.ibm.websphere.nd.doc/ae/txml_mapping.html

        String camelAttributeName = ATTRIBUTE_PREFIX + toCamelCase(attribute.getName()).replaceAll("\\W+", "");

        String javaType = xsdTypesToJava.getOrDefault(attribute.getType(), "Object");

        ClassWriter attributeWriter = generateClass(camelAttributeName, ABSTRACT_ATTRIBUTE_TYPE, null, "<" + javaType + ":" + JAVA_OBJECT_DESC + ">L" + ABSTRACT_ATTRIBUTE_TYPE + "<T" + javaType + ";>;", ACC_PUBLIC + ACC_SUPER, apiName);

        MethodVisitor mVisitor = attributeWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", "(T" + javaType + ";)V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ATTRIBUTE_TYPE, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        writeClassToFile(camelAttributeName, attributeWriter, apiName);
    }

    /**
     * Generates a default constructor.
     * @param classWriter The class writer from the class where the constructors will be added.
     * @param constructorType The modifiers for the constructor.
     */
    static void generateConstructor(ClassWriter classWriter, String baseClass, int constructorType, String apiName) {
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

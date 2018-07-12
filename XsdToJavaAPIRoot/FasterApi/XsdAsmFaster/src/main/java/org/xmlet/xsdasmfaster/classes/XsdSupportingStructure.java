package org.xmlet.xsdasmfaster.classes;

import javafx.util.Pair;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;

class XsdSupportingStructure {

    static final String JAVA_OBJECT = "java/lang/Object";
    static final String JAVA_OBJECT_DESC = "Ljava/lang/Object;";
    private static final String JAVA_STRING = "java/lang/String";
    static final String JAVA_STRING_DESC = "Ljava/lang/String;";
    private static final String JAVA_STRING_BUILDER = "java/lang/StringBuilder";
    private static final String JAVA_STRING_BUILDER_DESC = "Ljava/lang/StringBuilder;";
    static final String JAVA_LIST = "java/util/List";
    static final String JAVA_LIST_DESC = "Ljava/util/List;";
    static final String CONSTRUCTOR = "<init>";
    static final String STATIC_CONSTRUCTOR = "<clinit>";
    static final String ELEMENT = "Element";
    private static final String ATTRIBUTE = "Attribute";
    private static final String ABSTRACT_ELEMENT = "AbstractElement";
    private static final String BASE_ATTRIBUTE = "BaseAttribute";
    private static final String TEXT_CLASS = "Text";
    private static final String COMMENT_CLASS = "Comment";
    static final String TEXT_GROUP = "TextGroup";
    private static final String RESTRICTION_VIOLATION_EXCEPTION = "RestrictionViolationException";
    private static final String RESTRICTION_VALIDATOR = "RestrictionValidator";
    static final String VISITOR = "ElementVisitor";
    static final String ENUM_INTERFACE = "EnumInterface";
    static final String ATTRIBUTE_PREFIX = "Attr";

    private static String textType;
    static String textTypeDesc;
    private static String commentType;
    static String commentTypeDesc;
    static String abstractElementType;
    static String baseAttributeType;
    private static String baseAttributeTypeDesc;
    static String elementType;
    static String elementTypeDesc;
    private static String attributeType;
    static String attributeTypeDesc;
    private static String textGroupType;
    private static String restrictionViolationExceptionType;
    private static String restrictionValidatorType;
    static String elementVisitorType;
    static String elementVisitorTypeDesc;
    static String enumInterfaceType;

    private XsdSupportingStructure(){}

    /**
     * Creates the base infrastructure, based in the main three classes:
     * IElement - An interface containing the base operations of all elements.
     * IAttribute - An interface containing the base operations of all attributes.
     * AbstractElement - An abstract class from where all the elements will derive. It implements IElement.
     * Text - A concrete attribute with a different implementation that the other generated attributes.
     */
    static void createSupportingInfrastructure(String apiName) {
        textType = getFullClassTypeName(TEXT_CLASS, apiName);
        textTypeDesc = getFullClassTypeNameDesc(TEXT_CLASS, apiName);
        commentType = getFullClassTypeName(COMMENT_CLASS, apiName);
        commentTypeDesc = getFullClassTypeNameDesc(COMMENT_CLASS, apiName);
        abstractElementType = getFullClassTypeName(ABSTRACT_ELEMENT, apiName);
        baseAttributeType = getFullClassTypeName(BASE_ATTRIBUTE, apiName);
        baseAttributeTypeDesc = getFullClassTypeNameDesc(BASE_ATTRIBUTE, apiName);
        elementType = getFullClassTypeName(ELEMENT, apiName);
        elementTypeDesc = getFullClassTypeNameDesc(ELEMENT, apiName);
        attributeType = getFullClassTypeName(ATTRIBUTE, apiName);
        attributeTypeDesc = getFullClassTypeNameDesc(ATTRIBUTE, apiName);
        textGroupType = getFullClassTypeName(TEXT_GROUP, apiName);
        restrictionViolationExceptionType = getFullClassTypeName(RESTRICTION_VIOLATION_EXCEPTION, apiName);
        restrictionValidatorType = getFullClassTypeName(RESTRICTION_VALIDATOR, apiName);
        elementVisitorType = getFullClassTypeName(VISITOR, apiName);
        elementVisitorTypeDesc = getFullClassTypeNameDesc(VISITOR, apiName);
        enumInterfaceType = getFullClassTypeName(ENUM_INTERFACE, apiName);

        createElementInterface(apiName);
        createAttributeInterface(apiName);
        createEnumInterface(apiName);

        createTextGroupInterface(apiName);
        createTextElement(apiName);
        createCommentElement(apiName);

        createAbstractElement(apiName);
        createAttributeBase(apiName);

        createRestrictionValidator(apiName);
        createRestrictionViolationException(apiName);
    }

    /**
     * Generates the Element interface.
     * @param apiName The api this class will belong.
     */
    private static void createElementInterface(String apiName){
        ClassWriter classWriter = generateClass(ELEMENT, JAVA_OBJECT, null, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "self", "()" + elementTypeDesc, "()TT;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getVisitor", "()" + elementVisitorTypeDesc, null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getDepth", "()I", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "º", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getParent", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitEnd();

        writeClassToFile(ELEMENT, classWriter, apiName);
    }

    /**
     * Generates the IAttribute interface.
     * @param apiName The api this class will belong.
     */
    private static void createAttributeInterface(String apiName){
        ClassWriter classWriter = generateClass(ATTRIBUTE, JAVA_OBJECT, null, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getValue", "()" + JAVA_OBJECT_DESC, "()TT;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();

        writeClassToFile(ATTRIBUTE, classWriter, apiName);
    }

    /**
     * Adds a interface with a getValue value in order to extract the value from a enum element.
     * @param apiName The API this class will belong to.
     */
    private static void createEnumInterface(String apiName) {
        ClassWriter classWriter = generateClass(ENUM_INTERFACE, JAVA_OBJECT, null, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC + "", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getValue", "()" + JAVA_OBJECT_DESC + "", "()TT;", null);
        mVisitor.visitEnd();

        writeClassToFile(ENUM_INTERFACE, classWriter, apiName);
    }

    /**
     * Creates the text interface, allowing elements to have a text child node.
     * @param apiName The api this class will belong.
     */
    private static void createTextGroupInterface(String apiName) {
        ClassWriter classWriter = generateClass(TEXT_GROUP, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, TEXT_CLASS.toLowerCase(), "(" + JAVA_STRING_DESC + ")" + elementTypeDesc, "(" + JAVA_STRING_DESC + ")TT;", null);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, textType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, textType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, COMMENT_CLASS.toLowerCase(), "(" + JAVA_STRING_DESC + ")" + elementTypeDesc, "(" + JAVA_STRING_DESC + ")TT;", null);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, commentType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, commentType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        writeClassToFile(TEXT_GROUP, classWriter, apiName);
    }

    /**
     * Creates the Text class.
     * @param apiName The api this class will belong.
     */
    private static void createTextElement(String apiName) {
        createTextClass(TEXT_CLASS, textType, textTypeDesc, apiName);
    }

    /**
     * Creates the Text class.
     * @param apiName The api this class will belong.
     */
    private static void createCommentElement(String apiName) {
        createTextClass(COMMENT_CLASS, commentType, commentTypeDesc, apiName);
    }

    /**
     * Creates the Text class.
     * @param apiName The api this class will belong.
     */
    private static void createTextClass(String className, String classType, String classTypeDesc, String apiName) {
        ClassWriter classWriter = generateClass(className, abstractElementType, null,  "<Z::" + elementTypeDesc + ">L"  + abstractElementType +"<L" + classType + "<TZ;>;TZ;>;",ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "text", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementVisitorTypeDesc + "I)V", null, null);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("depth", "I", null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn("text");
        mVisitor.visitVarInsn(ILOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + elementVisitorTypeDesc + JAVA_STRING_DESC + "I)V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visit", "(" + classTypeDesc + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", "(TZ;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn("text");
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "text", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visit", "(" + classTypeDesc + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + classTypeDesc, "()L" + classType + "<TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "self", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(className, classWriter, apiName);
    }

    /**
     * Generates the AbstractElement class with all the implementations.
     */
    private static void createAbstractElement(String apiName){
        ClassWriter classWriter = generateClass(ABSTRACT_ELEMENT, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;",ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, apiName);
        FieldVisitor fVisitor;
        MethodVisitor mVisitor;

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        fVisitor = classWriter.visitField(ACC_PROTECTED, "name", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "parent", elementTypeDesc, "TZ;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "visitor", elementVisitorTypeDesc, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "depth", "I", null, null);
        fVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, CONSTRUCTOR, "(" + elementVisitorTypeDesc + JAVA_STRING_DESC + "I)V", null, null);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitLocalVariable("depth", "I", null, new Label(), new Label(),3);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ILOAD, 3);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "depth", "I");
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 4);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, CONSTRUCTOR, "(" + elementTypeDesc + "" + JAVA_STRING_DESC + ")V", "(TZ;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "getDepth", "()I", true);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitInsn(IADD);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "depth", "I");
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + "I)V", "(TZ;" + JAVA_STRING_DESC + "I)V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitLocalVariable("depth", "I", null, new Label(), new Label(),3);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ILOAD, 3);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "depth", "I");
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 4);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "º", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitParent", "(" + elementTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getParent", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getVisitor", "()" + elementVisitorTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getDepth", "()I", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "depth", "I");
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(ABSTRACT_ELEMENT, classWriter, apiName);
    }

    /**
     * Creates a abstract class for all concrete attributes, containing it's value.
     * @param apiName The api this class will belong.
     */
    private static void createAttributeBase(String apiName) {
        ClassWriter classWriter = generateClass(BASE_ATTRIBUTE, JAVA_OBJECT, new String[] {ATTRIBUTE}, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC + "L" + attributeType + "<TT;>;", ACC_PUBLIC + ACC_SUPER, apiName);

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "value", JAVA_OBJECT_DESC, "TT;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PRIVATE, "name", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED + ACC_STATIC, "restrictions", JAVA_LIST_DESC, "L" + JAVA_LIST + "<Ljava/util/Map<" + JAVA_STRING_DESC + JAVA_OBJECT_DESC + ">;>;", null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", "(TT;)V", null);
        mVisitor.visitLocalVariable("attrValue", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, baseAttributeType, "value", JAVA_OBJECT_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESTATIC, baseAttributeType, "getDefaultName", "(" + baseAttributeTypeDesc + ")" + JAVA_STRING_DESC, false);
        mVisitor.visitFieldInsn(PUTFIELD, baseAttributeType, "name", JAVA_STRING_DESC);
        mVisitor.visitFieldInsn(GETSTATIC, baseAttributeType, "restrictions", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + baseAttributeTypeDesc + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")V"), new Handle(Opcodes.H_INVOKESPECIAL, baseAttributeType, "validateRestrictions", "(Ljava/util/Map;)V", false), Type.getType("(Ljava/util/Map;)V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    
        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + "" + JAVA_STRING_DESC + ")V", "(TT;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitLocalVariable("attrValue", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, baseAttributeType, "value", JAVA_OBJECT_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, baseAttributeType, "name", JAVA_STRING_DESC);
        mVisitor.visitFieldInsn(GETSTATIC, baseAttributeType, "restrictions", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + baseAttributeTypeDesc + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")V"), new Handle(Opcodes.H_INVOKESPECIAL, baseAttributeType, "validateRestrictions", "(Ljava/util/Map;)V", false), Type.getType("(Ljava/util/Map;)V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_STATIC, STATIC_CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTSTATIC, baseAttributeType, "restrictions", JAVA_LIST_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 0);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_OBJECT_DESC + "", "()TT;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, baseAttributeType, "value", JAVA_OBJECT_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();
    
        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, baseAttributeType, "name", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();
    
        mVisitor = classWriter.visitMethod(ACC_STATIC, "getDefaultName", "(" + baseAttributeTypeDesc + ")" + JAVA_STRING_DESC, null, null);
        mVisitor.visitLocalVariable("attributeInstance", baseAttributeTypeDesc, null, new Label(), new Label(),0);
        mVisitor.visitLocalVariable("attributeName", JAVA_STRING_DESC, null, new Label(), new Label(),0);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_OBJECT, "getClass", "()Ljava/lang/Class;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()" + JAVA_STRING_DESC, false);
        mVisitor.visitLdcInsn("Attr");
        mVisitor.visitLdcInsn("");
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)" + JAVA_STRING_DESC, false);
        mVisitor.visitVarInsn(ASTORE, 1);
        lowerCaseFirst(mVisitor);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE, "validateRestrictions", "(Ljava/util/Map;)V", "(Ljava/util/Map<" + JAVA_STRING_DESC + JAVA_OBJECT_DESC + ">;)V", null);
        mVisitor.visitLocalVariable("restrictionMap", "Ljava/util/Map;", "Ljava/util/Map<" + JAVA_STRING_DESC + JAVA_OBJECT_DESC + ">;", new Label(), new Label(),1);
        mVisitor.visitLocalVariable("value", JAVA_OBJECT_DESC, "TT;", new Label(), new Label(), 2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, baseAttributeType, "getValue", "()" + JAVA_OBJECT_DESC, false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(INSTANCEOF, JAVA_STRING);
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(CHECKCAST, JAVA_STRING);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validate", "(Ljava/util/Map;" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {JAVA_OBJECT}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Integer");
        Label l1 = new Label();
        mVisitor.visitJumpInsn(IFNE, l1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Short");
        mVisitor.visitJumpInsn(IFNE, l1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Float");
        mVisitor.visitJumpInsn(IFNE, l1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Double");
        Label l2 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l2);
        mVisitor.visitLabel(l1);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Double");
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validate", "(Ljava/util/Map;Ljava/lang/Double;)V", false);
        mVisitor.visitLabel(l2);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(INSTANCEOF, JAVA_LIST);
        Label l3 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l3);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(CHECKCAST, JAVA_LIST);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validate", "(Ljava/util/Map;" + JAVA_LIST_DESC + ")V", false);
        mVisitor.visitLabel(l3);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        writeClassToFile(BASE_ATTRIBUTE, classWriter, apiName);
    }

    private static void lowerCaseFirst(MethodVisitor mVisitor){
        mVisitor.visitTypeInsn(NEW, JAVA_STRING_BUILDER);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_STRING_BUILDER, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "toLowerCase", "()" + JAVA_STRING_DESC, false);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "charAt", "(I)C", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "append", "(C)" + JAVA_STRING_BUILDER_DESC, false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "substring", "(I)" + JAVA_STRING_DESC, false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "append", "(" + JAVA_STRING_DESC + ")" + JAVA_STRING_BUILDER_DESC, false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "toString", "()" + JAVA_STRING_DESC, false);
    }

    /**
     * Creates a static class with method that validate all the XsdRestrictions.
     * @param apiName The api this class will belong.
     */
    private static void createRestrictionValidator(String apiName) {
        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor mVisitor;

        classWriter.visit(V1_8, ACC_PUBLIC + ACC_SUPER, restrictionValidatorType, null, JAVA_OBJECT, null);

        mVisitor = classWriter.visitMethod(ACC_PRIVATE, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_STATIC, "validate", "(Ljava/util/Map;" + JAVA_LIST_DESC + ")V", "(Ljava/util/Map<" + JAVA_STRING_DESC + "" + JAVA_OBJECT_DESC + ">;" + JAVA_LIST_DESC + ")V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("Length");
        validateIntegerRestriction(mVisitor);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateLength", "(I" + JAVA_LIST_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("MinLength");
        validateIntegerRestriction(mVisitor);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinLength", "(I" + JAVA_LIST_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("MaxLength");
        validateIntegerRestriction(mVisitor);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxLength", "(I" + JAVA_LIST_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_STATIC, "validate", "(Ljava/util/Map;Ljava/lang/Double;)V", "(Ljava/util/Map<" + JAVA_STRING_DESC + "" + JAVA_OBJECT_DESC + ">;Ljava/lang/Double;)V", null);
        mVisitor.visitCode();

        List<Pair<String, String>> names = new ArrayList<>();

        names.add(new Pair<>("MaxExclusive", "validateMaxExclusive"));
        names.add(new Pair<>("MaxInclusive", "validateMaxInclusive"));
        names.add(new Pair<>("MinExclusive", "validateMinExclusive"));
        names.add(new Pair<>("MinInclusive", "validateMinInclusive"));
        names.add(new Pair<>("FractionDigits", "validateFractionDigits"));
        names.add(new Pair<>("TotalDigits", "validateTotalDigits"));

        validateDoubleRestrictions(mVisitor, names);

        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_STATIC, "validate", "(Ljava/util/Map;" + JAVA_STRING_DESC + ")V", "(Ljava/util/Map<" + JAVA_STRING_DESC + "" + JAVA_OBJECT_DESC + ">;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("Length");
        validateIntegerRestriction(mVisitor);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateLength", "(I" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("MinLength");
        validateIntegerRestriction(mVisitor);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinLength", "(I" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("MaxLength");
        validateIntegerRestriction(mVisitor);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxLength", "(I" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("Pattern");
        mVisitor.visitInsn(ACONST_NULL);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(" + JAVA_OBJECT_DESC + "" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC + "", true);
        mVisitor.visitTypeInsn(CHECKCAST, JAVA_STRING);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validatePattern", "(" + JAVA_STRING_DESC + "" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateFractionDigits", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l2 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l2);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l2);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitInsn(D2I);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l3 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l3);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitMethodInsn(INVOKESTATIC, JAVA_STRING, "valueOf", "(D)" + JAVA_STRING_DESC, false);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitLdcInsn(",");
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "indexOf", "(" + JAVA_STRING_DESC + ")I", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "substring", "(I)" + JAVA_STRING_DESC, false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "length", "()I", false);
        mVisitor.visitVarInsn(ISTORE, 4);
        mVisitor.visitVarInsn(ILOAD, 4);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitJumpInsn(IF_ICMPLE, l3);
        throwRestrictionExceptionThreePartMessage(mVisitor, "Violation of fractionDigits restriction, value should have a maximum of ", " decimal places.");
        mVisitor.visitLabel(l3);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 5);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateLength", "(I" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l4 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l4);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l4);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "length", "()I", false);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l5 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPEQ, l5);
        throwRestrictionExceptionThreePartMessage(mVisitor, "Violation of length restriction, string should have exactly ", " characters.");
        mVisitor.visitLabel(l5);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateLength", "(I" + JAVA_LIST_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l6 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l6);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l6);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "size", "()I", true);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l7 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPEQ, l7);
        throwRestrictionExceptionThreePartMessage(mVisitor, "Violation of length restriction, list should have exactly ", " elements.");
        mVisitor.visitLabel(l7);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxExclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l8 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l8);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l8);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l9 = new Label();
        mVisitor.visitJumpInsn(IFLT, l9);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of maxExclusive restriction, value should be lesser than ");
        mVisitor.visitLabel(l9);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxInclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l10 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l10);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l10);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l11 = new Label();
        mVisitor.visitJumpInsn(IFLE, l11);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of maxInclusive restriction, value should be lesser or equal to ");
        mVisitor.visitLabel(l11);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxLength", "(I" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l12 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l12);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l12);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "length", "()I", false);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l13 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPLE, l13);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of maxLength restriction, string should have a max number of characters of ");
        mVisitor.visitLabel(l13);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxLength", "(I" + JAVA_LIST_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l14 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l14);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l14);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "size", "()I", true);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l15 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPLE, l15);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of maxLength restriction, list should have a max number of items of ");
        mVisitor.visitLabel(l15);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinExclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l16 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l16);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l16);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPG);
        Label l17 = new Label();
        mVisitor.visitJumpInsn(IFGT, l17);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of minExclusive restriction, value should be greater than ");
        mVisitor.visitLabel(l17);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinInclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l18 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l18);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l18);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPG);
        Label l19 = new Label();
        mVisitor.visitJumpInsn(IFGE, l19);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of minInclusive restriction, value should be greater or equal to ");
        mVisitor.visitLabel(l19);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinLength", "(I" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l20 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l20);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l20);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "length", "()I", false);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l21 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPGE, l21);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of minLength restriction, string should have a minimum number of characters of ");
        mVisitor.visitLabel(l21);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinLength", "(I" + JAVA_LIST_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l22 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l22);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l22);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "size", "()I", true);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l23 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPGE, l23);
        throwRestrictionExceptionTwoPartMessageInteger(mVisitor, "Violation of minLength restriction, list should have a minimum number of items of ");
        mVisitor.visitLabel(l23);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validatePattern", "(" + JAVA_STRING_DESC + "" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        Label l24 = new Label();
        mVisitor.visitJumpInsn(IFNONNULL, l24);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l24);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("");
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "replaceAll", "(" + JAVA_STRING_DESC + "" + JAVA_STRING_DESC + ")" + JAVA_STRING_DESC, false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "equals", "(" + JAVA_OBJECT_DESC + ")Z", false);
        Label l25 = new Label();
        mVisitor.visitJumpInsn(IFNE, l25);
        throwRestrictionExceptionTwoPartMessageString(mVisitor, "Violation of pattern restriction, the string doesn't math the acceptable pattern, which is ");
        mVisitor.visitLabel(l25);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateTotalDigits", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(ICONST_M1);
        Label l26 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPNE, l26);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitLabel(l26);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitMethodInsn(INVOKESTATIC, JAVA_STRING, "valueOf", "(D)" + JAVA_STRING_DESC, false);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitVarInsn(ISTORE, 4);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitInsn(D2I);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l27 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l27);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "length", "()I", false);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitInsn(ISUB);
        mVisitor.visitVarInsn(ISTORE, 4);
        Label l28 = new Label();
        mVisitor.visitJumpInsn(GOTO, l28);
        mVisitor.visitLabel(l27);
        mVisitor.visitFrame(Opcodes.F_APPEND,2, new Object[] {JAVA_STRING, Opcodes.INTEGER}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING, "length", "()I", false);
        mVisitor.visitVarInsn(ISTORE, 4);
        mVisitor.visitLabel(l28);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ILOAD, 4);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l29 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPEQ, l29);
        throwRestrictionExceptionThreePartMessage(mVisitor, "Violation of fractionDigits restriction, value should have a exactly ", " decimal places.");
        mVisitor.visitLabel(l29);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 5);
        mVisitor.visitEnd();

        writeClassToFile(RESTRICTION_VALIDATOR, classWriter, apiName);
    }

    private static void throwRestrictionExceptionTwoPartMessageInteger(MethodVisitor mVisitor, String firstMessagePart) {
        throwRestrictionExceptionTwoPartMessage(mVisitor, firstMessagePart, ILOAD, "I");
    }

    private static void throwRestrictionExceptionTwoPartMessageString(MethodVisitor mVisitor, String firstMessagePart) {
        throwRestrictionExceptionTwoPartMessage(mVisitor, firstMessagePart, ALOAD, JAVA_STRING_DESC);
    }

    private static void throwRestrictionExceptionTwoPartMessage(MethodVisitor mVisitor, String firstMessagePart, int instruction, String appendType) {
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitTypeInsn(NEW, JAVA_STRING_BUILDER);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_STRING_BUILDER, CONSTRUCTOR, "()V", false);
        mVisitor.visitLdcInsn(firstMessagePart);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "append", "(" + JAVA_STRING_DESC + ")" + JAVA_STRING_BUILDER_DESC, false);
        mVisitor.visitVarInsn(instruction, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "append", "(" + appendType + ")" + JAVA_STRING_BUILDER_DESC, false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "toString", "()" + JAVA_STRING_DESC, false);
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(ATHROW);

    }

    private static void throwRestrictionExceptionThreePartMessage(MethodVisitor mVisitor, String firstMessagePart, String thirdMessagePart) {
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitTypeInsn(NEW, JAVA_STRING_BUILDER);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_STRING_BUILDER, CONSTRUCTOR, "()V", false);
        mVisitor.visitLdcInsn(firstMessagePart);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "append", "(" + JAVA_STRING_DESC + ")" + JAVA_STRING_BUILDER_DESC, false);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "append", "(I)" + JAVA_STRING_BUILDER_DESC, false);
        mVisitor.visitLdcInsn(thirdMessagePart);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "append", "(" + JAVA_STRING_DESC + ")" + JAVA_STRING_BUILDER_DESC, false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_STRING_BUILDER, "toString", "()" + JAVA_STRING_DESC, false);
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(ATHROW);
    }

    private static void validateDoubleRestrictions(MethodVisitor mVisitor, List<Pair<String, String>> namePairs) {
        namePairs.forEach(namePair -> {
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn(namePair.getKey());
            validateIntegerRestriction(mVisitor);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, namePair.getValue(), "(ID)V", false);
        });
    }

    private static void validateIntegerRestriction(MethodVisitor mVisitor) {
        mVisitor.visitInsn(ICONST_M1);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(" + JAVA_OBJECT_DESC + "" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC + "", true);
        mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        mVisitor.visitVarInsn(ALOAD, 1);
    }

    /**
     * Creates the exception class that will be thrown if any restriction is violated.
     * @param apiName The api this class will belong.
     */
    private static void createRestrictionViolationException(String apiName) {
        ClassWriter classWriter = generateClass(RESTRICTION_VIOLATION_EXCEPTION, "java/lang/RuntimeException", null, null, ACC_PUBLIC + ACC_SUPER, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        writeClassToFile(RESTRICTION_VIOLATION_EXCEPTION, classWriter, apiName);
    }
}

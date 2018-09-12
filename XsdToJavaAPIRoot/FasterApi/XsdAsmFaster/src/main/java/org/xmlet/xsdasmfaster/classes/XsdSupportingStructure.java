package org.xmlet.xsdasmfaster.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;

class XsdSupportingStructure {

    static final String JAVA_OBJECT = "java/lang/Object";
    static final String JAVA_OBJECT_DESC = "Ljava/lang/Object;";
    static final String JAVA_STRING_DESC = "Ljava/lang/String;";
    static final String JAVA_LIST = "java/util/List";
    static final String JAVA_LIST_DESC = "Ljava/util/List;";
    static final String CONSTRUCTOR = "<init>";
    static final String STATIC_CONSTRUCTOR = "<clinit>";
    static final String ELEMENT = "Element";
    static final String CUSTOM_ATTRIBUTE_GROUP = "CustomAttributeGroup";
    static final String TEXT_GROUP = "TextGroup";
    private static final String RESTRICTION_VIOLATION_EXCEPTION = "RestrictionViolationException";
    private static final String RESTRICTION_VALIDATOR = "RestrictionValidator";
    static final String ELEMENT_VISITOR = "ElementVisitor";
    static final String ENUM_INTERFACE = "EnumInterface";
    static final String ATTRIBUTE_PREFIX = "Attr";
    private static final String TEXT = "Text";

    static String elementType;
    static String elementTypeDesc;
    static String restrictionValidatorType = "org/xmlet/xsdasmfaster/classes/infrastructure/RestrictionValidator";
    static String elementVisitorType;
    static String elementVisitorTypeDesc;
    static String enumInterfaceType = "org/xmlet/xsdasmfaster/classes/infrastructure/EnumInterface";
    private static String customAttributeGroupType;
    private static String textGroupType;
    static String textType;
    static String textTypeDesc;

    static Map<String, String> infrastructureVars;

    static {
        infrastructureVars = new HashMap<>();

        infrastructureVars.put(RESTRICTION_VALIDATOR, restrictionValidatorType);
        infrastructureVars.put(RESTRICTION_VIOLATION_EXCEPTION, "org/xmlet/xsdasmfaster/classes/infrastructure/RestrictionViolationException");
        infrastructureVars.put(ENUM_INTERFACE, enumInterfaceType);
    }

    private XsdSupportingStructure(){}

    static void createSupportingInfrastructure(String apiName){
        elementType = getFullClassTypeName(ELEMENT, apiName);
        elementTypeDesc = getFullClassTypeNameDesc(ELEMENT, apiName);
        elementVisitorType = getFullClassTypeName(ELEMENT_VISITOR, apiName);
        elementVisitorTypeDesc = getFullClassTypeNameDesc(ELEMENT_VISITOR, apiName);
        textGroupType = getFullClassTypeName(TEXT_GROUP, apiName);
        customAttributeGroupType = getFullClassTypeName(CUSTOM_ATTRIBUTE_GROUP, apiName);
        textType = getFullClassTypeName(TEXT, apiName);
        textTypeDesc = getFullClassTypeNameDesc(TEXT, apiName);

        createElement(apiName);
        createTextGroup(apiName);
        createCustomAttributeGroup(apiName);
        createText(apiName);

        infrastructureVars.put(ELEMENT, elementType);
        infrastructureVars.put(ELEMENT_VISITOR, elementVisitorType);
        infrastructureVars.put(TEXT_GROUP, textGroupType);
    }

    private static void createElement(String apiName) {
        ClassWriter classWriter = generateClass(ELEMENT, JAVA_OBJECT, null, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "self", "()" + elementTypeDesc, "()TT;", null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getVisitor", "()" + elementVisitorTypeDesc, null, null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "º", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getParent", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitEnd();
        writeClassToFile(ELEMENT, classWriter, apiName);
    }

    private static void createTextGroup(String apiName){
        ClassWriter classWriter = generateClass(TEXT_GROUP, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, "text", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, "<R:" + JAVA_OBJECT_DESC + ">(TR;)TT;", null);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitTypeInsn(NEW, textType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, textType, CONSTRUCTOR, "(" + elementTypeDesc + elementVisitorTypeDesc + JAVA_OBJECT_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitText", "(" + textTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(6, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "comment", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, "<R:" + JAVA_OBJECT_DESC + ">(TR;)TT;", null);
        mVisitor.visitLocalVariable("comment", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitTypeInsn(NEW, textType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, textType, CONSTRUCTOR, "(" + elementTypeDesc + elementVisitorTypeDesc + JAVA_OBJECT_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitComment", "(" + textTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(6, 2);
        mVisitor.visitEnd();

        writeClassToFile(TEXT_GROUP, classWriter, apiName);
    }

    private static void createCustomAttributeGroup(String apiName){
        ClassWriter classWriter = generateClass(CUSTOM_ATTRIBUTE_GROUP, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + JAVA_STRING_DESC + JAVA_STRING_DESC +")" + elementTypeDesc, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC +")TT;", null);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("value", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, customAttributeGroupType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitAttribute", "(" + JAVA_STRING_DESC + JAVA_STRING_DESC +")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, customAttributeGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        writeClassToFile(CUSTOM_ATTRIBUTE_GROUP, classWriter, apiName);
    }

    private static void createText(String apiName){
        ClassWriter classWriter = generateClass(TEXT, JAVA_OBJECT, new String[] {ELEMENT}, "<Z::" + elementTypeDesc + "R:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<L" + textType + "<TZ;TR;>;TZ;>;", ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE + ACC_FINAL, "text", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED + ACC_FINAL, "parent", elementTypeDesc, "TZ;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED + ACC_FINAL, "visitor", elementVisitorTypeDesc, null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + elementVisitorTypeDesc + JAVA_OBJECT_DESC + ")V", "(TZ;" + elementVisitorTypeDesc + "TR;)V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, textType, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, textType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_OBJECT, "toString", "()" + JAVA_STRING_DESC, false);
        mVisitor.visitFieldInsn(PUTFIELD, textType, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 4);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + textTypeDesc, "()L" + textType + "<TZ;TR;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "º", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, textType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitText", "(" + textTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, textType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getParent", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, textType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitLdcInsn("");
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getVisitor", "()" + elementVisitorTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, textType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, textType, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, textType, "self", "()" + textTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(TEXT, classWriter, apiName);
    }
}

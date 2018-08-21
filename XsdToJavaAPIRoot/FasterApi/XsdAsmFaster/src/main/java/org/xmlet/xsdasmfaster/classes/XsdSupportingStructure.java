package org.xmlet.xsdasmfaster.classes;

import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;

class XsdSupportingStructure {

    static final String JAVA_OBJECT = "java/lang/Object";
    static final String JAVA_OBJECT_DESC = "Ljava/lang/Object;";
    private static final String JAVA_STRING = "java/lang/String";
    static final String JAVA_STRING_DESC = "Ljava/lang/String;";
    static final String JAVA_LIST = "java/util/List";
    static final String JAVA_LIST_DESC = "Ljava/util/List;";
    static final String CONSTRUCTOR = "<init>";
    static final String STATIC_CONSTRUCTOR = "<clinit>";
    static final String ELEMENT = "Element";
    private static final String TEXT_CLASS = "Text";
    private static final String COMMENT_CLASS = "Comment";
    static final String TEXT_GROUP = "TextGroup";
    private static final String RESTRICTION_VIOLATION_EXCEPTION = "RestrictionViolationException";
    private static final String RESTRICTION_VALIDATOR = "RestrictionValidator";
    static final String VISITOR = "ElementVisitor";
    static final String ENUM_INTERFACE = "EnumInterface";
    static final String ATTRIBUTE_PREFIX = "Attr";

    static String elementType = "org/xmlet/xsdasmfaster/classes/infrastructure/Element";
    static String elementTypeDesc = "Lorg/xmlet/xsdasmfaster/classes/infrastructure/Element;";
    static String textGroupType = "org/xmlet/xsdasmfaster/classes/infrastructure/TextGroup";
    private static String restrictionViolationExceptionType = "org/xmlet/xsdasmfaster/classes/infrastructure/RestrictionViolationException";
    static String restrictionValidatorType = "org/xmlet/xsdasmfaster/classes/infrastructure/RestrictionValidator";
    static String elementVisitorType = "org/xmlet/xsdasmfaster/classes/infrastructure/ElementVisitor";
    static String elementVisitorTypeDesc = "Lorg/xmlet/xsdasmfaster/classes/infrastructure/ElementVisitor;";
    static String enumInterfaceType = "org/xmlet/xsdasmfaster/classes/infrastructure/EnumInterface";

    static Map<String, String> infrastructureVars;

    static {
        infrastructureVars = new HashMap<>();

        infrastructureVars.put(ELEMENT, elementType);
        infrastructureVars.put(TEXT_GROUP, textGroupType);
        infrastructureVars.put(RESTRICTION_VALIDATOR, restrictionValidatorType);
        infrastructureVars.put(RESTRICTION_VIOLATION_EXCEPTION, restrictionViolationExceptionType);
        infrastructureVars.put(VISITOR, elementVisitorType);
        infrastructureVars.put(ENUM_INTERFACE, enumInterfaceType);
    }

    private XsdSupportingStructure(){}

    /**
     * Creates the base infrastructure, based in the main three classes:
     * IElement - An interface containing the base operations of all elements.
     * IAttribute - An interface containing the base operations of all attributes.
     * AbstractElement - An abstract class from where all the elements will derive. It implements IElement.
     * Text - A concrete attribute with a different implementation that the other generated attributes.
     */
    static void createSupportingInfrastructure(String apiName) {
        textGroupType = getFullClassTypeName(TEXT_GROUP, apiName);
        restrictionViolationExceptionType = getFullClassTypeName(RESTRICTION_VIOLATION_EXCEPTION, apiName);
        restrictionValidatorType = getFullClassTypeName(RESTRICTION_VALIDATOR, apiName);
        elementVisitorType = getFullClassTypeName(VISITOR, apiName);
        elementVisitorTypeDesc = getFullClassTypeNameDesc(VISITOR, apiName);
        enumInterfaceType = getFullClassTypeName(ENUM_INTERFACE, apiName);

        /*
        createElementInterface(apiName);
        createEnumInterface(apiName);
        createTextGroupInterface(apiName);

        createRestrictionValidator(apiName);
        createRestrictionViolationException(apiName);
        */
    }

    /**
     * Generates the Element interface.
     * @param apiName The api this class will belong.
     */
    /*
    private static void createElementInterface(String apiName){
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
    */

    /**
     * Adds a interface with a getValue value in order to extract the value from a enum element.
     * @param apiName The API this class will belong to.
     */
    /*
    private static void createEnumInterface(String apiName) {
        ClassWriter classWriter = generateClass(ENUM_INTERFACE, JAVA_OBJECT, null, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC + "", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getValue", "()" + JAVA_OBJECT_DESC + "", "()TT;", null);
        mVisitor.visitEnd();

        writeClassToFile(ENUM_INTERFACE, classWriter, apiName);
    }
    */

    /**
     * Creates the text interface, allowing elements to have a text child node.
     * @param apiName The api this class will belong.
     */
    /*
    private static void createTextGroupInterface(String apiName) {
        ClassWriter classWriter = generateClass(TEXT_GROUP, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, TEXT_CLASS.toLowerCase(), "(" + JAVA_STRING_DESC + ")" + elementTypeDesc, "(" + JAVA_STRING_DESC + ")TT;", null);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitText", "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, COMMENT_CLASS.toLowerCase(), "(" + JAVA_STRING_DESC + ")" + elementTypeDesc, "(" + JAVA_STRING_DESC + ")TT;", null);
        mVisitor.visitLocalVariable("comment", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitComment", "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        writeClassToFile(TEXT_GROUP, classWriter, apiName);
    }
    */

    /**
     * Creates a static class with method that validate all the XsdRestrictions.
     * @param apiName The api this class will belong.
     */
    /*
    private static void createRestrictionValidator(String apiName) {
        ClassWriter classWriter = new ClassWriter(0);

        classWriter.visit(V1_8, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, restrictionValidatorType, null, JAVA_OBJECT, null);

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PRIVATE, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateFractionDigits", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitInsn(D2I);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l0);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitLdcInsn(",");
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "indexOf", "(Ljava/lang/String;)I", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mVisitor.visitVarInsn(ISTORE, 4);
        mVisitor.visitVarInsn(ILOAD, 4);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitJumpInsn(IF_ICMPLE, l0);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of fractionDigits restriction, value should have a maximum of \u0001 decimal places.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 5);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateLength", "(ILjava/lang/String;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l1 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPEQ, l1);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of length restriction, string should have exactly \u0001 characters.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l1);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateLength", "(ILjava/util/List;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l2 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPEQ, l2);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of length restriction, list should have exactly \u0001 elements.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l2);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMaxExclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l3 = new Label();
        mVisitor.visitJumpInsn(IFLT, l3);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of maxExclusive restriction, value should be lesser than \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l3);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMaxInclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l4 = new Label();
        mVisitor.visitJumpInsn(IFLE, l4);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of maxInclusive restriction, value should be lesser or equal to \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l4);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMaxLength", "(ILjava/lang/String;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l5 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPLE, l5);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of maxLength restriction, string should have a max number of characters of \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l5);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMaxLength", "(ILjava/util/List;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l6 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPLE, l6);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of maxLength restriction, list should have a max number of items of \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l6);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMinExclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPG);
        Label l7 = new Label();
        mVisitor.visitJumpInsn(IFGT, l7);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of minExclusive restriction, value should be greater than \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l7);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMinInclusive", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPG);
        Label l8 = new Label();
        mVisitor.visitJumpInsn(IFGE, l8);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of minInclusive restriction, value should be greater or equal to \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l8);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMinLength", "(ILjava/lang/String;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l9 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPGE, l9);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of minLength restriction, string should have a minimum number of characters of \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l9);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateMinLength", "(ILjava/util/List;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l10 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPGE, l10);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of minLength restriction, list should have a minimum number of items of \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l10);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validatePattern", "(Ljava/lang/String;Ljava/lang/String;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("");
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        Label l11 = new Label();
        mVisitor.visitJumpInsn(IFNE, l11);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/String;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of pattern restriction, the string doesn't math the acceptable pattern, which is \u0001");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l11);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateTotalDigits", "(ID)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitVarInsn(DLOAD, 1);
        mVisitor.visitInsn(D2I);
        mVisitor.visitInsn(I2D);
        mVisitor.visitInsn(DCMPL);
        Label l12 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l12);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitInsn(ISUB);
        mVisitor.visitVarInsn(ISTORE, 4);
        Label l13 = new Label();
        mVisitor.visitJumpInsn(GOTO, l13);
        mVisitor.visitLabel(l12);
        mVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/lang/String"}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mVisitor.visitVarInsn(ISTORE, 4);
        mVisitor.visitLabel(l13);
        mVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
        mVisitor.visitVarInsn(ILOAD, 4);
        mVisitor.visitVarInsn(ILOAD, 0);
        Label l14 = new Label();
        mVisitor.visitJumpInsn(IF_ICMPEQ, l14);
        mVisitor.visitTypeInsn(NEW, restrictionViolationExceptionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ILOAD, 0);
        mVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), "Violation of fractionDigits restriction, value should have a exactly \u0001 decimal places.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, restrictionViolationExceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitLabel(l14);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 5);
        mVisitor.visitEnd();

        classWriter.visitEnd();

        writeClassToFile(RESTRICTION_VALIDATOR, classWriter, apiName);
    }
    */

    /**
     * Creates the exception class that will be thrown if any restriction is violated.
     * @param apiName The api this class will belong.
     */
    /*
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
    */
}

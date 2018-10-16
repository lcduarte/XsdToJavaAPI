package org.xmlet.xsdasmfaster.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdasmfaster.classes.infrastructure.EnumInterface;
import org.xmlet.xsdasmfaster.classes.infrastructure.RestrictionValidator;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;

/**
 * This class is responsible for creating some infrastructure classes that can't be reused.
 */
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

    /**
     * Type name for the Element interface.
     */
    static String elementType;

    /**
     * Type descriptor for the Element interface.
     */
    static String elementTypeDesc;

    /**
     * Type name for the {@link RestrictionValidator} class present in the infrastructure package of this solution.
     */
    static String restrictionValidatorType = "org/xmlet/xsdasmfaster/classes/infrastructure/RestrictionValidator";

    /**
     * Type name for the ElementVisitor abstract class.
     */
    static String elementVisitorType;

    /**
     * Type descriptor for the ElementVisitor abstract class.
     */
    static String elementVisitorTypeDesc;

    /**
     * Type name for the {@link EnumInterface} interface present in the infrastructure package of this solution.
     */
    static String enumInterfaceType = "org/xmlet/xsdasmfaster/classes/infrastructure/EnumInterface";

    /**
     * Type name for the CustomAttributeGroup interface.
     */
    private static String customAttributeGroupType;

    /**
     * Type name for the TextGroup interface.
     */
    private static String textGroupType;

    /**
     * Type name for the Text class.
     */
    static String textType;

    /**
     * Type descriptor for the Text class.
     */
    static String textTypeDesc;

    /**
     * A {@link Map} object with information regarding types that are present in the infrastructure package of this
     * solution. Their name resolution differs from all the remaining classes since their name is already defined.
     */
    static Map<String, String> infrastructureVars;

    static {
        infrastructureVars = new HashMap<>();

        infrastructureVars.put(RESTRICTION_VALIDATOR, restrictionValidatorType);
        infrastructureVars.put(RESTRICTION_VIOLATION_EXCEPTION, "org/xmlet/xsdasmfaster/classes/infrastructure/RestrictionViolationException");
        infrastructureVars.put(ENUM_INTERFACE, enumInterfaceType);
    }

    private XsdSupportingStructure(){}

    /**
     * Creates all the classes that belong to the fluent interface infrastructure and can't be defined the same way
     * as the classes present in the infrastructure package of this solution.
     * @param apiName The name of the generated fluent interface.
     */
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

    /**
     * Creates the Element interface.
     * Methods:
     *  T self();
     *  Z __();
     *  Z getParent();
     *  String getName();
     *  ElementVisitor getVisitor();
     * @param apiName The name of the generated fluent interface.
     */
    private static void createElement(String apiName) {
        ClassWriter classWriter = generateClass(ELEMENT, JAVA_OBJECT, null, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "self", "()" + elementTypeDesc, "()TT;", null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getVisitor", "()" + elementVisitorTypeDesc, null, null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "__", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitEnd();
        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getParent", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitEnd();

        writeClassToFile(ELEMENT, classWriter, apiName);
    }

    /**
     * Creates the TextGroup interface.
     * Methods:
     *  <R> T text(R text)
     *  <R> T comment(R comment)
     * @param apiName The name of the generated fluent interface.
     */
    private static void createTextGroup(String apiName){
        ClassWriter classWriter = generateClass(TEXT_GROUP, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        textGroupMethod(classWriter, "text", "visitText");
        textGroupMethod(classWriter, "comment", "visitComment");

        writeClassToFile(TEXT_GROUP, classWriter, apiName);
    }

    /**
     * Creates a method present in the TextGroup interface.
     * Code:
     *  getVisitor().visitComment(new Text<>(self(), getVisitor(), text));
     *  return this.self();
     * @param classWriter The TextGroup {@link ClassWriter} object.
     * @param varName The name of the method, i.e. text or comment.
     * @param visitName The name of the visit method to invoke on the method, i.e. visitText or visitComment.
     */
    private static void textGroupMethod(ClassWriter classWriter, String varName, String visitName){
        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, varName, "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, "<R:" + JAVA_OBJECT_DESC + ">(TR;)TT;", null);
        mVisitor.visitLocalVariable(varName, JAVA_STRING_DESC, null, new Label(), new Label(),1);
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
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, visitName, "(" + textTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(6, 2);
        mVisitor.visitEnd();
    }

    /**
     * Creates the CustomAttributeGroup interface. This interface allows the insertion of attributes that weren't defined
     * in the XSD file of the language.
     * Code:
     *  T addCustomAttr(String name, String value) {
     *      getVisitor().visitAttribute(name, value);
     *      return this.self();
     *  }
     * @param apiName The name of the generated fluent interface.
     */
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

    /**
     * Creates the Text class.
     *
     * Fields:
     *  String text;
     *  Z parent;
     *  ElementVisitor visitor;
     *
     * Methods:
     *  public Text<P, R> self() {
     *      return this;
     *  }
     *
     *  public P __() {
     *      visitor.visitText(this);
     *      return parent;
     *  }
     *
     *  public P getParent() {
     *      return parent;
     *  }
     *
     *  public String getName() {
     *      return "";
     *  }
     *
     *  public Visitor getVisitor() {
     *      return visitor;
     *  }
     *
     *  public String getValue() {
     *      return text;
     *  }
     * @param apiName The name of the generated fluent interface.
     */
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

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "__", "()" + elementTypeDesc, "()TZ;", null);
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

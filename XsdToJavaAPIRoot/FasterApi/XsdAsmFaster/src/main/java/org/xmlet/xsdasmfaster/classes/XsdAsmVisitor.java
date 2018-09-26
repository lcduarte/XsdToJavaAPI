package org.xmlet.xsdasmfaster.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdNamedElements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasmfaster.classes.XsdSupportingStructure.*;

/**
 * This class has the responsibility of creating the ElementVisitor class.
 */
class XsdAsmVisitor {

    private static final String VISIT_ELEMENT_NAME = "visitElement";
    private static final String VISIT_PARENT_NAME = "visitParent";
    private static final String VISIT_ATTRIBUTE_NAME = "visitAttribute";

    private XsdAsmVisitor(){}

    /**
     * Generates both the abstract visitor class with methods for each element from the list.
     * @param elementNames The elements names list.
     * @param apiName The name of the generated fluent interface.
     */
    static void generateVisitors(Set<String> elementNames, List<XsdAttribute> attributes, String apiName){
        generateVisitorInterface(elementNames, filterAttributes(attributes), apiName);
    }

    /**
     * Generates the visitor class for this fluent interface with methods for all elements in the element list.
     *
     * Main methods:
     *  void visitElement(Element element);
     *  void visitAttribute(String attributeName, String attributeValue);
     *  void visitParent(Element elementName);
     *  <R> void visitText(Text<? extends Element, R> text);
     *  <R> void visitComment(Text<? extends Element, R> comment);
     * @param elementNames The elements names list.
     * @param attributes The list of attributes to be generated.
     * @param apiName The name of the generated fluent interface.
     */
    private static void generateVisitorInterface(Set<String> elementNames, List<XsdAttribute> attributes, String apiName) {
        ClassWriter classWriter = generateClass(ELEMENT_VISITOR, JAVA_OBJECT, null, null, ACC_PUBLIC + ACC_ABSTRACT + ACC_SUPER, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_ELEMENT_NAME, "(" + elementTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_ATTRIBUTE_NAME, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_PARENT_NAME, "(" + elementTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "visitText", "(" + textTypeDesc + ")V", "<R:" + JAVA_OBJECT_DESC + ">(L" + textType + "<+" + elementTypeDesc + "TR;>;)V", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "visitComment", "(" + textTypeDesc + ")V", "<R:" + JAVA_OBJECT_DESC + ">(L" + textType + "<+" + elementTypeDesc + "TR;>;)V", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "visitOpenDynamic", "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(0, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "visitCloseDynamic", "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(0, 1);
        mVisitor.visitEnd();

        elementNames.forEach(elementName -> addVisitorParentMethod(classWriter, elementName, apiName));

        elementNames.forEach(elementName -> addVisitorElementMethod(classWriter, elementName, apiName));

        attributes.forEach(attribute -> addVisitorAttributeMethod(classWriter, attribute));

        writeClassToFile(ELEMENT_VISITOR, classWriter, apiName);
    }

    /**
     * Adds a specific method for a visitAttribute call.
     * Example:
     *  void visitAttributeManifest(String manifestValue){
     *      visitAttribute("manifest", manifestValue);
     *  }
     * @param classWriter The ElementVisitor class {@link ClassWriter}.
     * @param attribute The specific attribute.
     */
    private static void addVisitorAttributeMethod(ClassWriter classWriter, XsdAttribute attribute) {
        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, VISIT_ATTRIBUTE_NAME + getCleanName(attribute.getName()), "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable(firstToLower(getCleanName(attribute.getName())), JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn(attribute.getRawName());
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, VISIT_ATTRIBUTE_NAME, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();
    }

    /**
     * Adds a specific method for a visitElement call.
     * Example:
     *  void visitElementHtml(Html<Z> html){
     *      visitElement(html);
     *  }
     * @param classWriter The ElementVisitor class {@link ClassWriter}.
     * @param elementName The specific element.
     * @param apiName The name of the generated fluent interface.
     */
    @SuppressWarnings("Duplicates")
    private static void addVisitorElementMethod(ClassWriter classWriter, String elementName, String apiName) {
        elementName = getCleanName(elementName);
        String classType = getFullClassTypeName(elementName, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(elementName, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, VISIT_ELEMENT_NAME + elementName, "(" + classTypeDesc + ")V", "<Z::" + elementTypeDesc + ">(L" + classType + "<TZ;>;)V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, VISIT_ELEMENT_NAME, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }

    /**
     * Adds a specific method for a visitParent call.
     * Example:
     *  void visitParentHtml(Html<Z> html){
     *      visitParent(html);
     *  }
     * @param classWriter The ElementVisitor class {@link ClassWriter}.
     * @param elementName The specific element.
     * @param apiName The name of the generated fluent interface.
     */
    @SuppressWarnings("Duplicates")
    private static void addVisitorParentMethod(ClassWriter classWriter, String elementName, String apiName) {
        elementName = getCleanName(elementName);
        String classType = getFullClassTypeName(elementName, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(elementName, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, VISIT_PARENT_NAME + getCleanName(elementName), "(" + classTypeDesc + ")V", "<Z::" + elementTypeDesc + ">(L" + classType + "<TZ;>;)V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, VISIT_PARENT_NAME, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }

    /**
     * Removes duplicate attribute names.
     * @param attributes The {@link List} of {@link XsdAttribute} objects.
     * @return The distinct {@link List} of {@link XsdAttribute}.
     */
    private static List<XsdAttribute> filterAttributes(List<XsdAttribute> attributes) {
        List<String> attributeNames = attributes.stream()
                .map(XsdNamedElements::getName)
                .distinct()
                .collect(Collectors.toList());

        List<XsdAttribute> filteredAttributes = new ArrayList<>();

        attributeNames.forEach(attributeName -> {
            for (XsdAttribute attribute : attributes) {
                if (attribute.getName().equals(attributeName)){
                    filteredAttributes.add(attribute);
                    break;
                }
            }
        });

        return filteredAttributes;
    }
}

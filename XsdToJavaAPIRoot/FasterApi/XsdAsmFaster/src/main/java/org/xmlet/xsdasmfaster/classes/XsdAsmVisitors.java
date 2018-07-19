package org.xmlet.xsdasmfaster.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasmfaster.classes.XsdSupportingStructure.*;

class XsdAsmVisitors {

    private static final String VISIT_METHOD_NAME = "visit";
    
    private XsdAsmVisitors(){}

    /**
     * Generates both the visitor interface and abstract visitor with method for each element from the list.
     * @param elementNames The elements names list.
     * @param apiName The api this classes will belong to.
     */
    static void generateVisitors(Set<String> elementNames, List<String> attributes, String apiName){
        generateVisitorInterface(elementNames, attributes, apiName);
    }

    /**
     * Generates the visitor class for this api with methods for all elements in the element list.
     * @param elementNames The elements names list.
     * @param attributes The list of attributes to be generated.
     * @param apiName The api this class will belong to.
     */
    private static void generateVisitorInterface(Set<String> elementNames, List<String> attributes, String apiName) {
        ClassWriter classWriter = generateClass(VISITOR, JAVA_OBJECT, null, null, ACC_PUBLIC + ACC_ABSTRACT + ACC_SUPER, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_METHOD_NAME, "(" + elementTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_METHOD_NAME, "("  + attributeTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "visitParent", "(" + elementTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_METHOD_NAME, "("  + textTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC +ACC_ABSTRACT, VISIT_METHOD_NAME, "(" + commentTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        elementNames.forEach(elementName -> addVisitorInterfaceMethod(classWriter, elementName, elementTypeDesc, apiName));

        attributes.forEach(attribute -> addVisitorInterfaceMethod(classWriter, attribute, attributeTypeDesc, apiName));

        writeClassToFile(VISITOR, classWriter, apiName);
    }

    /**
     * Adds methods for each element to the visitor interface.
     * @param classWriter The Visitor interface class writer.
     * @param elementName The element for which the methods will be generated.
     * @param apiName The api name from the Visitor interface.
     */
    private static void addVisitorInterfaceMethod(ClassWriter classWriter, String elementName, String paramType, String apiName){
        String methodTypeDesc = getFullClassTypeNameDesc(toCamelCase(getCleanName(elementName)), apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, VISIT_METHOD_NAME, "(" + methodTypeDesc + ")V", null, null);
        mVisitor.visitLocalVariable(toCamelCase(elementName), methodTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, VISIT_METHOD_NAME, "(" + paramType + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }
}
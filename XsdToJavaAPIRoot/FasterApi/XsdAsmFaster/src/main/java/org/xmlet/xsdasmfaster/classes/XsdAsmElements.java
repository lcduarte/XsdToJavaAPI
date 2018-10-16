package org.xmlet.xsdasmfaster.classes;

import org.objectweb.asm.*;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasmfaster.classes.XsdSupportingStructure.*;

/**
 * This class is responsible to generate all the code that is element related.
 */
class XsdAsmElements {

    private XsdAsmElements(){}

    /**
     * Generates a class based on the information present in a {@link XsdElement} object. It also generated its
     * constructors and required methods.
     * @param interfaceGenerator An instance of {@link XsdAsmInterfaces} which contains interface information.
     * @param createdAttributes Information regarding attribute classes that were already created.
     * @param element The {@link XsdElement} object from which the class will be generated.
     * @param apiName The name of the generated fluent interface.
     */
    static void generateClassFromElement(XsdAsmInterfaces interfaceGenerator, Map<String, List<XsdAttribute>> createdAttributes, XsdElement element, String apiName) {
        String className = getCleanName(element);

        String[] interfaces = interfaceGenerator.getInterfaces(element, apiName);
        String signature = getClassSignature(interfaces, className, apiName);

        ClassWriter classWriter = generateClass(className, JAVA_OBJECT, interfaces, signature,ACC_PUBLIC + ACC_SUPER + ACC_FINAL, apiName);

        generateClassMethods(classWriter, className, apiName);

        interfaceGenerator.checkForSequenceMethod(classWriter, className);

        getOwnAttributes(element).forEach(elementAttribute -> generateMethodsAndCreateAttribute(createdAttributes, classWriter, elementAttribute, getFullClassTypeNameDesc(className, apiName), className, apiName));

        writeClassToFile(className, classWriter, apiName);
    }

    /**
     * Creates some class specific methods that all implementations of {@link XsdAbstractElement} should have, which are:
     *  Constructor(ElementVisitor visitor) - Assigns the argument to the visitor field;
     *  Constructor(Element parent)         - Assigns the argument to the parent field and obtains the visitor of the parent;
     *  Constructor(Element parent, ElementVisitor visitor, boolean performsVisit) -
     *                                        An alternative constructor to avoid the visit method call;
     *  of({@link Consumer} consumer)       - Method used to avoid variable extraction in order to allow cleaner code;
     *  dynamic({@link Consumer} consumer)  - Method used to indicate that the changes on the fluent interface performed
     *                                        inside the Consumer code are a dynamic aspect of the result and are bound to change;
     *  self()                              - Returns this;
     *  getName()                           - Returns the name of the element;
     *  getParent()                         - Returns the parent field;
     *  getVisitor()                        - Returns the visitor field;
     *  __()                                - Returns the parent and calls the respective visitParent method.
     * @param classWriter The {@link ClassWriter} on which the methods should be written.
     * @param className The class name.
     * @param apiName The name of the generated fluent interface.
     */
    private static void generateClassMethods(ClassWriter classWriter, String className, String apiName) {
        generateClassMethods(classWriter, className, className, apiName, true);
    }

    /**
     * Creates some class specific methods that all implementations of {@link XsdAbstractElement} should have, which are:
     *  Constructor(ElementVisitor visitor) - Assigns the argument to the visitor field;
     *  Constructor(Element parent)         - Assigns the argument to the parent field and obtains the visitor of the parent;
     *  Constructor(Element parent, ElementVisitor visitor, boolean performsVisit) -
     *                                        An alternative constructor to avoid the visit method call;
     *  of({@link Consumer} consumer)       - Method used to avoid variable extraction in order to allow cleaner code;
     *  dynamic({@link Consumer} consumer)  - Method used to indicate that the changes on the fluent interface performed
     *                                        inside the Consumer code are a dynamic aspect of the result and are bound to change;
     *  self()                              - Returns this;
     *  getName()                           - Returns the name of the element;
     *  getParent()                         - Returns the parent field;
     *  getVisitor()                        - Returns the visitor field;
     *  __()                                - Returns the parent and calls the respective visitParent method.
     * @param classWriter The {@link ClassWriter} on which the methods should be written.
     * @param className The class name.
     * @param apiName The name of the generated fluent interface.
     * @param performVisits Indicates if the visit method call should be performed in the
     *                      Constructor(Element parent, ElementVisitor visitor, boolean performsVisit) method.
     */
    static void generateClassMethods(ClassWriter classWriter, String typeName, String className, String apiName, boolean performVisits) {
        String classType = getFullClassTypeName(typeName, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(typeName, apiName);
        String name = firstToLower(className);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PROTECTED + ACC_FINAL, "parent", elementTypeDesc, "TZ;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED + ACC_FINAL, "visitor", elementVisitorTypeDesc, null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementVisitorTypeDesc + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ACONST_NULL);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "parent", elementTypeDesc);

        if (performVisits){
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitElement" + getCleanName(className), "(" + classTypeDesc + ")V", false);
        }

        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + ")V", "(TZ;)V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "visitor", elementVisitorTypeDesc);

        if (performVisits) {
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitFieldInsn(GETFIELD, classType, "visitor", elementVisitorTypeDesc);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitElement" + getCleanName(className), "(" + classTypeDesc + ")V", false);
        }

        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();


        mVisitor = classWriter.visitMethod(ACC_PROTECTED, CONSTRUCTOR, "(" + elementTypeDesc + elementVisitorTypeDesc + "Z)V", "(TZ;" + elementVisitorTypeDesc + "Z)V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),2);
        mVisitor.visitLocalVariable("shouldVisit", "Z", null, new Label(), new Label(),3);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "visitor", elementVisitorTypeDesc);

        if (performVisits) {
            mVisitor.visitVarInsn(ILOAD, 3);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IFEQ, l0);
            mVisitor.visitVarInsn(ALOAD, 2);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitElement" + getCleanName(className), "(" + classTypeDesc + ")V", false);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_FULL, 4, new Object[]{classType, elementType, elementVisitorType, Opcodes.INTEGER}, 0, new Object[]{});
        }

        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 4);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "__", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitParent" + getCleanName(typeName), "(" + classTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "dynamic", "(Ljava/util/function/Consumer;)" + classTypeDesc, "(Ljava/util/function/Consumer<L" + classType + "<TZ;>;>;)L" + classType + "<TZ;>;", null);
        mVisitor.visitLocalVariable("consumer", "Ljava/util/function/Consumer;", null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitOpenDynamic", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(" + JAVA_OBJECT_DESC + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitCloseDynamic", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "of", "(Ljava/util/function/Consumer;)" + classTypeDesc, "(Ljava/util/function/Consumer<L" + classType + "<TZ;>;>;)L" + classType + "<TZ;>;", null);
        mVisitor.visitLocalVariable("consumer", "Ljava/util/function/Consumer;", null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(" + JAVA_OBJECT_DESC + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getParent", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "getVisitor", "()" + elementVisitorTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "visitor", elementVisitorTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitLdcInsn(name);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "self", "()" + classTypeDesc, "()L" + classType + "<TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
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
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The {@link ClassWriter} where the method will be written.
     * @param child The child of the element which generated the class. Their name represents a method.
     * @param classType The type of the class which contains the children elements.
     * @param apiName The name of the generated fluent interface.
     */
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String apiName) {
        generateMethodsForElement(classWriter, child.getName(), classType, apiName, new String[]{});
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The {@link ClassWriter} where the method will be written.
     * @param childName The child name that represents a method.
     * @param classType The type of the class which contains the children elements.
     * @param apiName The name of the generated fluent interface.
     * @param annotationsDesc An array with annotation names to apply to the generated method.
     */
    static void generateMethodsForElement(ClassWriter classWriter, String childName, String classType, String apiName, String[] annotationsDesc) {
        childName = firstToLower(getCleanName(childName));
        String childCamelName = firstToUpper(childName);
        String childType = getFullClassTypeName(childCamelName, apiName);
        String childTypeDesc = getFullClassTypeNameDesc(childCamelName, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, childName, "()" + childTypeDesc, "()L" + childType + "<TT;>;", null);

        for (String annotationDesc: annotationsDesc) {
            mVisitor.visitAnnotation(annotationDesc, true);
        }

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 1);
        mVisitor.visitEnd();
    }
}

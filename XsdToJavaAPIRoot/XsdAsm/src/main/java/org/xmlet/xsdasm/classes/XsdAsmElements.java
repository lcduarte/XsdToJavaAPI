package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdparser.xsdelements.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

class XsdAsmElements {

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param interfaceGenerator An instance of XsdAsmInterfaces which contains interface information.
     * @param createdAttributes A list of names of attribute classes already created.
     * @param element The element from which the class will be generated.
     * @param apiName The api this class will belong.
     */
    static void generateClassFromElement(XsdAsmInterfaces interfaceGenerator, Map<String, List<XsdAttribute>> createdAttributes, XsdElement element, String apiName) {
        String className = toCamelCase(element.getName());

        XsdElement base = null;

        if (element.getName().equals("LinearLayout")){
            int a  = 5;
        }

        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            XsdComplexContent complexContent = complexType.getComplexContent();

            if (complexContent != null){
                XsdExtension extension = complexContent.getXsdExtension();

                if (extension != null){
                    base = extension.getBase();
                }
            }
        }

        Stream<XsdAttribute> elementAttributes = getOwnAttributes(element);
        String[] interfaces = interfaceGenerator.getInterfaces(element, apiName);

        String signature = getClassSignature(base, interfaces, className, apiName);

        String superType = base == null ? ABSTRACT_ELEMENT_TYPE : getFullClassTypeName(toCamelCase(base.getName()), apiName);

        ClassWriter classWriter = generateClass(className, superType, interfaces, signature,ACC_PUBLIC + ACC_SUPER, apiName);

        generateClassSpecificMethods(classWriter, className, apiName, superType, null);

        elementAttributes.forEach(elementAttribute -> generateMethodsAndCreateAttribute(createdAttributes, classWriter, elementAttribute, getFullClassTypeNameDesc(className, apiName), apiName));

        writeClassToFile(className, classWriter, apiName);
    }

    /**
     * Creates some class specific methods that all implementations of AbstractElement should have, which are:
     * A constructor with a String parameter, which is it will create a Text attribute in the created element.
     * A constructor with two String parameters, the first being the value of the Text attribute, and the second being a value for its id.
     * An implementation of the self method, which should return this.
     * @param classWriter The class writer on which should be written the methods.
     * @param className The class name.
     * @param defaultName The defaultName for this element.
     */
    static void generateClassSpecificMethods(ClassWriter classWriter, String className, String apiName, String superType, String defaultName) {
        String classType = getFullClassTypeName(className, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(className, apiName);
        String name;

        if (defaultName != null){
            name = defaultName;
        } else {
            name = firstToLower(className);
        }

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn(name);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, CONSTRUCTOR, "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(Ljava/lang/String;)V", null, null);
        mVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, "<init>", "(Ljava/lang/String;)V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + ELEMENT_TYPE_DESC + ")V", "(TP;)V", null);
        mVisitor.visitLocalVariable("parent", ELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn(name);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, CONSTRUCTOR, "(" + ELEMENT_TYPE_DESC + "Ljava/lang/String;)V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(" + ELEMENT_TYPE_DESC + "Ljava/lang/String;)V", "(TP;Ljava/lang/String;)V", null);
        mVisitor.visitLocalVariable("parent", ELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, "<init>", "(" + ELEMENT_TYPE_DESC + "Ljava/lang/String;)V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + classTypeDesc, "()L" + classType + "<TP;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + ELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "self", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "accept", "(" + ELEMENT_VISITOR_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", ELEMENT_VISITOR_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, ELEMENT_VISITOR_TYPE, "visit", "(" + classTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$accept$0", "(" + ELEMENT_VISITOR_TYPE_DESC + ELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, ELEMENT_TYPE, "accept", "(" + ELEMENT_VISITOR_TYPE_DESC + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "cloneElem", "()" + ELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "cloneElem", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "cloneElem", "()" + classTypeDesc, "()L" + classType + "<TP;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, classType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, classType, "<init>", "()V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "clone", "(" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC , false);
        mVisitor.visitTypeInsn(CHECKCAST, classType);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 1);
        mVisitor.visitEnd();
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param child The child of the element which generated the class. Their name represents a method.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String returnType, String apiName) {
        generateMethodsForElement(classWriter, child.getName(), classType, returnType, apiName);
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param childName The name of the element which generated the class.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, String childName, String classType, String returnType, String apiName) {
        String childCamelName = toCamelCase(childName);
        String childType = getFullClassTypeName(childCamelName, apiName);
        String childTypeDesc = getFullClassTypeNameDesc(childCamelName, apiName);
        boolean isInterface = isInterfaceMethod(returnType);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, childName, "()" + childTypeDesc, "()L" + childType + "<TT;>;", null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "self", "()" + ELEMENT_TYPE_DESC, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + ELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);

        if (isInterface){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + ELEMENT_TYPE_DESC + ")" + ELEMENT_TYPE_DESC, true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + ELEMENT_TYPE_DESC + ")" + ELEMENT_TYPE_DESC, false);
        }

        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();
    }
}

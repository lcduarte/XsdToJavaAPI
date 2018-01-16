package XsdAsm;

import XsdElements.XsdAttribute;
import XsdElements.XsdElement;
import org.objectweb.asm.*;

import java.util.List;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmUtils.*;
import static XsdAsm.XsdSupportingStructure.*;
import static org.objectweb.asm.Opcodes.*;

class XsdAsmElements {

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param interfaceGenerator An instance of XsdAsmInterfaces which contains interface information.
     * @param createdAttributes A list of names of attribute classes already created.
     * @param element The element from which the class will be generated.
     * @param apiName The api this class will belong.
     */
    static void generateClassFromElement(XsdAsmInterfaces interfaceGenerator, List<String> createdAttributes, XsdElement element, String apiName) {
        String className = toCamelCase(element.getName());

        Stream<XsdElement> elementChildren = getOwnChildren(element);
        Stream<XsdAttribute> elementAttributes = getOwnAttributes(element);
        String[] interfaces = interfaceGenerator.getInterfaces(element);

        String signature = getClassSignature(interfaces, className, apiName);

        ClassWriter classWriter = generateClass(className, ABSTRACT_ELEMENT_TYPE, interfaces, signature,ACC_PUBLIC + ACC_SUPER, apiName);

        generateConstructor(classWriter, ABSTRACT_ELEMENT_TYPE, ACC_PUBLIC);

        generateClassSpecificMethods(classWriter, className, apiName);

        elementChildren.forEach(child -> generateMethodsForElement(classWriter, child, getFullClassTypeName(className, apiName), getFullClassTypeNameDesc(className, apiName), apiName));

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
     */
    private static void generateClassSpecificMethods(ClassWriter classWriter, String className, String apiName) {
        String classType = getFullClassTypeName(className, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(className, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);
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
        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),2);
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

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "accept", "(" + VISITOR_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", VISITOR_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, VISITOR_TYPE, "initVisit", "(" + classTypeDesc + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "getChildren", "()Ljava/util/List;", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + VISITOR_TYPE_DESC + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKESTATIC, classType, "lambda$accept$0", "(" + VISITOR_TYPE_DESC + IELEMENT_TYPE_DESC + ")V", false), Type.getType("(" + IELEMENT_TYPE_DESC + ")V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, VISITOR_TYPE, "endVisit", "(" + classTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$accept$0", "(" + VISITOR_TYPE_DESC + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "accept", "(" + VISITOR_TYPE_DESC + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
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
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String returnType, String apiName) {
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

        if (isInterfaceMethod(returnType)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();


        if (isInterfaceMethod(returnType)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + ")" + returnType, "(" + JAVA_STRING_DESC + ")" + returnType, null);
        }

        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);

        if (isInterfaceMethod(returnType)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (isInterfaceMethod(returnType)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();


        if (isInterfaceMethod(returnType)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + returnType, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + returnType, null);
        }

        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),2);

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 3);

        if (isInterfaceMethod(returnType)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (isInterfaceMethod(returnType)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + returnType, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 4);
        mVisitor.visitEnd();
    }
}

package XsdClassGenerator;

import XsdElements.XsdElement;
import XsdElements.XsdElementBase;
import XsdElements.XsdGroup;
import XsdElements.XsdMultipleElements;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class XsdClassGenerator {

    private static final String BASE_CLASS = "BaseElement";
    private static final String JAVA_OBJECT = "java/lang/Object";
    private static final String JAVA_STRING = "Ljava/lang/String;";
    private static final String CONSTRUCTOR = "<init>";

    private Map<String, List<XsdElement>> groupInterfaces = new HashMap<>();

    public void generateClassFromElements(List<XsdElement> elementList){
        generateBaseElement();

        elementList.forEach(this::generateClassFromElement);

        generateInterfaces();
    }

    private void generateBaseElement() {
        List<String> fields = XsdClassGeneratorUtils.getElementFields();

        ClassWriter classWriter = generateClass(BASE_CLASS);

        generateProtectedConstructor(classWriter, fields, BASE_CLASS);

        generateFields(classWriter, fields, BASE_CLASS);

        classWriter.visitEnd();

        XsdClassGeneratorUtils.createGeneratedFilesDirectory();
        XsdClassGeneratorUtils.writeClassToFile(BASE_CLASS, classWriter.toByteArray());
    }

    private void generateClassFromElement(XsdElement element) {
        String className = XsdClassGeneratorUtils.toCamelCase(element.getName());

        List<String> fields = XsdClassGeneratorUtils.getElementFields();
        List<XsdElement> childs = getOwnChilds(element);
        String[] interfaces = getSharedInterfaces(element);

        ClassWriter classWriter = generateClass(className, XsdClassGeneratorUtils.getFullClassTypeName(BASE_CLASS), interfaces);

        generatePublicConstructor(classWriter, fields, className);

        generateMethods(classWriter, childs);

        classWriter.visitEnd();

        XsdClassGeneratorUtils.writeClassToFile(className, classWriter.toByteArray());
    }

    private void generateInterfaces() {
        groupInterfaces.keySet().forEach(this::generateInterface);
    }

    private void generateInterface(String interfaceName){
        ClassWriter interfaceWriter = generateClass(interfaceName, JAVA_OBJECT, new String[]{}, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE);

        generateMethods(interfaceWriter, groupInterfaces.get(interfaceName));

        interfaceWriter.visitEnd();

        XsdClassGeneratorUtils.writeClassToFile(interfaceName, interfaceWriter.toByteArray());
    }

    private void generateMethods(ClassWriter classWriter, List<XsdElement> childs) {
        for (XsdElement child : childs) {
            if (child.getName() == null && child.getRef().equals("math")){
                continue;
            }

            String childCamelName = XsdClassGeneratorUtils.toCamelCase(child.getName());

            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "()" + XsdClassGeneratorUtils.getFullClassName(childCamelName), null, null);
            methodVisitor.visitCode();
            methodVisitor.visitTypeInsn(NEW, XsdClassGeneratorUtils.getFullClassTypeName(childCamelName));
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, XsdClassGeneratorUtils.getFullClassTypeName(childCamelName), CONSTRUCTOR, "()V", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(2, 1);
            methodVisitor.visitEnd();
        }
    }

    private void generateFields(ClassWriter classWriter, List<String> fields, String className) {
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        String fullClassName = XsdClassGeneratorUtils.getFullClassTypeName(className);

        for (String name : fields) {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE, name, JAVA_STRING, null, null);
            fieldVisitor.visitEnd();

            String camelCaseName = XsdClassGeneratorUtils.toCamelCase(name);

            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "get" + camelCaseName, "()" + JAVA_STRING, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, fullClassName, name, JAVA_STRING);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();

            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "set" + camelCaseName, "(" + JAVA_STRING +")V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, fullClassName, name, JAVA_STRING);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
    }

    private void generateProtectedConstructor(ClassWriter classWriter, List<String> fields, String className){
        generateConstructor(classWriter, fields, className, ACC_PROTECTED);
    }

    private void generatePublicConstructor(ClassWriter classWriter, List<String> fields, String className) {
        generateConstructor(classWriter, fields, className, ACC_PUBLIC);
    }

    private void generateConstructor(ClassWriter classWriter, List<String> fields, String className, int constructorType) {
        String constructorParameters = "(" + String.join("", Collections.nCopies(fields.size(),JAVA_STRING)) + ")V";

        MethodVisitor constructorWParameters = classWriter.visitMethod(constructorType, CONSTRUCTOR, constructorParameters,null,null);
        MethodVisitor defaultConstructor = classWriter.visitMethod(constructorType, CONSTRUCTOR, "()V",null,null);

        constructorWParameters.visitCode();
        defaultConstructor.visitCode();

        defaultConstructor.visitVarInsn(ALOAD, 0);
        constructorWParameters.visitVarInsn(ALOAD, 0);

        if (className.equals(BASE_CLASS)) {
            constructorWParameters.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
            defaultConstructor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);

            int index = 1;

            for (String name : fields) {
                constructorWParameters.visitVarInsn(ALOAD, 0);
                constructorWParameters.visitVarInsn(ALOAD, index);
                constructorWParameters.visitFieldInsn(PUTFIELD, XsdClassGeneratorUtils.getFullClassTypeName(className), name, JAVA_STRING);
                index = index + 1;
            }
        } else {
            for(int i = 0; i < fields.size(); ++i ){
                constructorWParameters.visitVarInsn(ALOAD, i + 1);
            }

            constructorWParameters.visitMethodInsn(INVOKESPECIAL, XsdClassGeneratorUtils.getFullClassTypeName(BASE_CLASS), CONSTRUCTOR, constructorParameters, false);
            defaultConstructor.visitMethodInsn(INVOKESPECIAL, XsdClassGeneratorUtils.getFullClassTypeName(BASE_CLASS), CONSTRUCTOR, "()V", false);
        }

        constructorWParameters.visitInsn(RETURN);
        constructorWParameters.visitMaxs(1, fields.size() + 1);

        defaultConstructor.visitInsn(RETURN);
        defaultConstructor.visitMaxs(1, 1);

        constructorWParameters.visitEnd();
        defaultConstructor.visitEnd();
    }

    private ClassWriter generateClass(String className, String superName, String[] interfaces, int classModifiers) {
        ClassWriter classWriter = new ClassWriter(0);

        classWriter.visit(V1_8, classModifiers, XsdClassGeneratorUtils.getFullClassTypeName(className), null, superName, interfaces);

        return classWriter;
    }

    private ClassWriter generateClass(String className, String superName, String[] interfaces) {
        return generateClass(className, superName, interfaces, ACC_PUBLIC);
    }

    private ClassWriter generateClass(String className) {
        return generateClass(className, JAVA_OBJECT, new String[]{}, ACC_PUBLIC);
    }

    private String[] getSharedInterfaces(XsdElement element){
        //Groups in XSD will be equivalent to java Interfaces.
        if (element.getComplexType() != null){
            XsdElementBase elementBase = element.getComplexType().getChildElement();

            if (elementBase instanceof XsdMultipleElements){
                XsdMultipleElements multipleElementContainer = (XsdMultipleElements) elementBase;

                Map<String, List<XsdElement>> groupElements = multipleElementContainer.getGroupElements();

                return getInterfaceNames(groupElements);
            }
        }

        return new String[0];
    }

    private String[] getInterfaceNames(Map<String, List<XsdElement>> groupElements) {
        String[] groupNames = new String[groupElements.keySet().size()];
        groupNames = groupElements.keySet().toArray(groupNames);

        groupElements.keySet().forEach(groupName -> {
            if (!groupInterfaces.containsKey(groupName)){
                groupInterfaces.put(XsdClassGeneratorUtils.getInterfaceName(groupName), groupElements.get(groupName));
            }
        });

        for (int i = 0; i < groupNames.length ; i++) {
            groupNames[i] = XsdClassGeneratorUtils.getInterfaceName(groupNames[i]);
        }

        return groupNames;
    }

    private List<XsdElement> getOwnChilds(XsdElement element) {
        if (element.getComplexType() != null){
            XsdElementBase elementBase = element.getComplexType().getChildElement();

            if (elementBase != null) {
                if (elementBase instanceof XsdGroup){
                    return ((XsdGroup) elementBase).getChildElement().getElements();
                }

                if (elementBase instanceof XsdMultipleElements) {
                    return ((XsdMultipleElements) elementBase).getElements();
                }
            }
        }

        return new ArrayList<>();
    }

}

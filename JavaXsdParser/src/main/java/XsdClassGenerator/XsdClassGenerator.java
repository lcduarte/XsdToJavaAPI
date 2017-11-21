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

    private static final String PACKAGE = "XsdClassGenerator/ParsedObjects/";
    private static final String BASE_CLASS = "BaseElement";
    private static final String JAVA_OBJECT = "java/lang/Object";
    private static final String JAVA_STRING = "Ljava/lang/String;";
    private static final String CONSTRUCTOR = "<init>";

    private Map<String, List<XsdElement>> groupInterfaces = new HashMap<>();

    //TODO Create folder for ParsedObjects

    private void generateBaseElement() {
        List<String> fields = getElementFields();

        ClassWriter classWriter = generateClass(BASE_CLASS);

        generateProtectedConstructor(classWriter, fields, BASE_CLASS);

        generateFields(classWriter, fields, BASE_CLASS);

        classWriter.visitEnd();

        writeClassToFile(BASE_CLASS, classWriter.toByteArray());
    }

    public void generateClassFromElements(List<XsdElement> elementList){
        generateBaseElement();

        elementList.forEach(this::generateClassFromElement);

        generateInterfaces();
    }

    private void generateClassFromElement(XsdElement element) {
        String className = toCamelCase(element.getName());

        List<String> fields = getElementFields();
        List<XsdElement> childs = getOwnChilds(element);
        String[] interfaces = getSharedInterfaces(element);

        ClassWriter classWriter = generateClass(className, getFullClassTypeName(BASE_CLASS), interfaces);

        generatePublicConstructor(classWriter, fields, className);

        generateMethods(classWriter, childs);

        classWriter.visitEnd();

        writeClassToFile(className, classWriter.toByteArray());
    }

    private void generateInterfaces() {
        groupInterfaces.keySet().forEach(this::generateInterface);
    }

    private void generateInterface(String interfaceName){
        ClassWriter interfaceWriter = generateClass(interfaceName, JAVA_OBJECT, new String[]{}, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE);

        generateMethods(interfaceWriter, groupInterfaces.get(interfaceName));

        interfaceWriter.visitEnd();

        writeClassToFile(interfaceName, interfaceWriter.toByteArray());
    }

    private void generateMethods(ClassWriter classWriter, List<XsdElement> childs) {
        for (XsdElement child : childs) {
            if (child.getName() == null && child.getRef().equals("math")){
                continue;
            }

            String childCamelName = toCamelCase(child.getName());

            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "()" + getFullClassName(childCamelName), null, null);
            methodVisitor.visitCode();
            methodVisitor.visitTypeInsn(NEW, getFullClassTypeName(childCamelName));
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, getFullClassTypeName(childCamelName), CONSTRUCTOR, "()V", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(2, 1);
            methodVisitor.visitEnd();
        }
    }

    private void generateFields(ClassWriter classWriter, List<String> fields, String className) {
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        String fullClassName = getFullClassTypeName(className);

        for (String name : fields) {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE, name, JAVA_STRING, null, null);
            fieldVisitor.visitEnd();

            String camelCaseName = toCamelCase(name);

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
                constructorWParameters.visitFieldInsn(PUTFIELD, getFullClassTypeName(className), name, JAVA_STRING);
                index = index + 1;
            }
        } else {
            for(int i = 0; i < fields.size(); ++i ){
                constructorWParameters.visitVarInsn(ALOAD, i + 1);
            }

            constructorWParameters.visitMethodInsn(INVOKESPECIAL, getFullClassTypeName(BASE_CLASS), CONSTRUCTOR, constructorParameters, false);
            defaultConstructor.visitMethodInsn(INVOKESPECIAL, getFullClassTypeName(BASE_CLASS), CONSTRUCTOR, "()V", false);
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

        classWriter.visit(V1_8, classModifiers, getFullClassTypeName(className), null, superName, interfaces);

        return classWriter;
    }

    private ClassWriter generateClass(String className, String superName, String[] interfaces) {
        return generateClass(className, superName, interfaces, ACC_PUBLIC);
    }

    private ClassWriter generateClass(String className) {
        return generateClass(className, JAVA_OBJECT, new String[]{}, ACC_PUBLIC);
    }


    //Groups in XSD will be equivalent to java Interfaces.
    private String[] getSharedInterfaces(XsdElement element){
        if (element.getComplexType() == null){
            return new String[0];
        }

        XsdElementBase elementBase = element.getComplexType().getChildElement();

        if (elementBase instanceof XsdMultipleElements){
            XsdMultipleElements multipleElementContainer = (XsdMultipleElements) elementBase;

            Map<String, List<XsdElement>> groupElements = multipleElementContainer.getGroupElements();

            for (String groupName : groupElements.keySet()) {
                if (!groupInterfaces.containsKey(groupName)){
                    groupInterfaces.put("I" + toCamelCase(groupName), groupElements.get(groupName));
                }
            }

            String[] groupNames = new String[groupElements.keySet().size()];
            groupNames = groupElements.keySet().toArray(groupNames);

            for (int i = 0; i < groupNames.length ; i++) {
                groupNames[i] = "I" + toCamelCase(groupNames[i]);
            }

            return groupNames;
        }

        return new String[0];
    }

    private List<XsdElement> getOwnChilds(XsdElement element) {
        if (element.getComplexType() == null){
            return new ArrayList<>();
        }

        XsdElementBase elementBase = element.getComplexType().getChildElement();

        if (elementBase instanceof XsdGroup){
            return ((XsdGroup) elementBase).getChildElement().getElements();
        } else {
            if (elementBase == null){
                return new ArrayList<>();
            }

            return ((XsdMultipleElements) elementBase).getElements();
        }
    }

    private String toCamelCase(String name){
        if (name.length() == 1){
            return name.toUpperCase();
        }

        String firstLetter = name.substring(0, 1).toUpperCase();
        return firstLetter + name.substring(1);
    }

    private String getFinalPathPart(String className){
        return "\\XsdClassGenerator\\ParsedObjects\\" + className + ".class";
    }

    private String getFullClassTypeName(String className){
        return PACKAGE + className;
    }

    private String getFullClassName(String className){
        return "L" + PACKAGE + className + ";";
    }

    private List<String> getElementFields() {
        List<String> fields = new ArrayList<>();

        fields.add(XsdElement.ID);
        fields.add(XsdElement.MIN_OCCURS);
        fields.add(XsdElement.MAX_OCCURS);

        fields.add(XsdElement.NAME);

        fields.add(XsdElement.TYPE);
        fields.add(XsdElement.ABSTRACT);
        fields.add(XsdElement.FORM);
        fields.add(XsdElement.FIXED);
        fields.add(XsdElement.FINAL);
        fields.add(XsdElement.SUBSTITUTION_GROUP);
        fields.add(XsdElement.DEFAULT);
        fields.add(XsdElement.NILLABLE);
        fields.add(XsdElement.BLOCK);

        return fields;
    }

    private void writeClassToFile(String className, byte[] constructedClass){
        try {
            FileOutputStream os = new FileOutputStream(new File(XsdClassGenerator.class.getClassLoader().getResource("").getPath() + getFinalPathPart(className)));
            os.write(constructedClass);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package XsdClassGenerator;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.*;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.*;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class XsdClassGenerator {

    private static final String BASE_CLASS = "BaseElement";
    private static final String JAVA_OBJECT = "java/lang/Object";
    private static final String JAVA_STRING = "Ljava/lang/String;";
    private static final String CONSTRUCTOR = "<init>";

    private Map<String, List<ConcreteElement>> groupInterfaces = new HashMap<>();

    public void generateClassFromElements(List<ReferenceBase> elementList){
        List<ConcreteElement> concreteElementList = elementList.stream()
                                                                .filter(element -> element instanceof ConcreteElement)
                                                                .map(element -> (ConcreteElement) element)
                                                                .filter(element -> element.getElement() instanceof XsdElement)
                                                                .collect(Collectors.toList());

        generateBaseElement();

        concreteElementList.forEach(this::generateClassFromElement);

        generateInterfaces();
    }

    /**
     * Generates a BaseElement which will contain all the behaviour/attributes common to all the generated classes
     */
    private void generateBaseElement() {
        List<String> fields = XsdClassGeneratorUtils.getElementFields();

        ClassWriter classWriter = generateClass(BASE_CLASS);

        generateProtectedConstructor(classWriter, fields, BASE_CLASS);

        generateFields(classWriter, fields, BASE_CLASS);

        classWriter.visitEnd();

        XsdClassGeneratorUtils.createGeneratedFilesDirectory();
        XsdClassGeneratorUtils.writeClassToFile(BASE_CLASS, classWriter.toByteArray());
    }

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param elementWrapper The wrapper which contains the element from which the class will be generated.
     */
    private void generateClassFromElement(ConcreteElement elementWrapper) {
        if (elementWrapper.getElement() instanceof XsdElement){
            XsdElement element = (XsdElement) elementWrapper.getElement();

            String className = XsdClassGeneratorUtils.toCamelCase(element.getName());

            List<String> fields = XsdClassGeneratorUtils.getElementFields();
            List<ConcreteElement> childs = getOwnChildren(element);
            String[] interfaces = getSharedInterfaces(element);

            ClassWriter classWriter = generateClass(className, XsdClassGeneratorUtils.getFullClassTypeName(BASE_CLASS), interfaces);

            generatePublicConstructor(classWriter, fields, className);

            generateMethods(classWriter, childs);

            classWriter.visitEnd();

            XsdClassGeneratorUtils.writeClassToFile(className, classWriter.toByteArray());
        }
    }

    private void generateInterfaces() {
        groupInterfaces.keySet().forEach(this::generateInterface);
    }

    /**
     * Generates a interface with all its methods. It uses the information gathered about in groupInterfaces
     * @param interfaceName The interface name.
     */
    private void generateInterface(String interfaceName){
        ClassWriter interfaceWriter = generateClass(interfaceName, JAVA_OBJECT, new String[]{}, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE);

        generateMethods(interfaceWriter, groupInterfaces.get(interfaceName));

        interfaceWriter.visitEnd();

        XsdClassGeneratorUtils.writeClassToFile(interfaceName, interfaceWriter.toByteArray());
    }

    /**
     * Generates all the method from a given class.
     * @param classWriter The class where the method will be written.
     * @param childs The children of the element which generated the class. Their name represents a
     *               class name from another class.
     */
    private void generateMethods(ClassWriter classWriter, List<ConcreteElement> childs) {
        for (ConcreteElement child : childs) {
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

    /**
     * Generates field for a given class.
     * @param classWriter The class where the fields will be added.
     * @param fields The name of the fields to be created. (Only String fields are being supported)
     * @param className The class name from the class where the field will be added.
     */
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

    /**
     * Generates a default constructor and another with all the parameters.
     * @param classWriter The class writer from the class where the constructors will be added.
     * @param fields The name of the fields of this class, needed for making the constructor with the parameters.
     * @param className The class name
     * @param constructorType The modifiers for the constructor
     */
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

    /**
     * Generates an empty class.
     * @param className The classes name.
     * @param superName The super object, which the class extends from.
     * @param interfaces The name of the interfaces which this class implements.
     * @param classModifiers The modifiers to the class.
     * @return A class writer that will be used to write the remaining information of the class.
     */
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

    /**
     * This method obtains the interfaces which his class will be implementing.
     * The interfaces are represented in XsdElements as XsdGroups, and their respective.
     * methods as children of the XsdGroup.
     * @param element The element from which the interfaces will be obtained.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getSharedInterfaces(XsdElement element){
        //Groups in XSD will be equivalent to java Interfaces.
        String[] typeInterfaces = new String[0], groupInterfaces = new String[0];
        ReferenceBase typeWrapper = element.getType();

        if (typeWrapper != null &&
                typeWrapper instanceof ConcreteElement &&
                typeWrapper.getElement() != null &&
                typeWrapper.getElement() instanceof XsdComplexType){

            typeInterfaces = getSharedInterfaces((XsdComplexType) typeWrapper.getElement());
        }

        if (element.getComplexType() != null){
            groupInterfaces = getSharedInterfaces(element.getComplexType());
        }

        return ArrayUtils.addAll(typeInterfaces, groupInterfaces);
    }

    private String[] getSharedInterfaces(XsdComplexType complexType) {
        ReferenceBase elementWrapper = complexType.getChildElement();

        if (elementWrapper instanceof ConcreteElement){
            ConcreteElement complexTypeChild = (ConcreteElement) elementWrapper;

            XsdElementBase r = complexTypeChild.getElement();
            Map<String, List<ReferenceBase>> groupElements = new HashMap<>();

            if (r instanceof XsdGroup){
                groupElements.put(((XsdGroup) r).getName(), r.getElements());
            }

            if (r instanceof XsdMultipleElements){
                groupElements = ((XsdMultipleElements) r).getGroupElements();
            }

            return getInterfaceNames(groupElements);
        }

        return new String[0];
    }

    /**
     * This method will populate the groupInterfaces field with all the interface information
     * that will be obtained while creating the classes in order to create all the needed interfaces
     * afterwards.
     * @param groupElements The Map containing the information about interfaces from a given element.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getInterfaceNames(Map<String, List<ReferenceBase>> groupElements) {
        String[] groupNames = new String[groupElements.keySet().size()];
        groupNames = groupElements.keySet().toArray(groupNames);

        groupElements.keySet().forEach((String groupName) -> {
            if (!groupInterfaces.containsKey(groupName)){
                groupInterfaces.put(
                        XsdClassGeneratorUtils.getInterfaceName(groupName),
                        groupElements.get(groupName)
                                    .stream()
                                    .filter(element -> element instanceof ConcreteElement)
                                    .map(element -> (ConcreteElement) element)
                                    .collect(Collectors.toList()));
            }
        });

        for (int i = 0; i < groupNames.length ; i++) {
            groupNames[i] = XsdClassGeneratorUtils.getInterfaceName(groupNames[i]);
        }

        return groupNames;
    }

    /**
     * Returns all the concrete children of a given element. With the separation made between
     * XsdGroups children and the remaining children this method is able to return only the
     * children that are not shared in any interface.
     * @param element The element from which the children will be obtained.
     * @return The children that are exclusive to the current element.
     */
    private List<ConcreteElement> getOwnChildren(XsdElement element) {
        if (element.getComplexType() != null){
            ReferenceBase elementWrapper = element.getComplexType().getChildElement();

            if (elementWrapper != null) {
                if (elementWrapper instanceof ConcreteElement){
                    return elementWrapper.getElement()
                            .getElements()
                            .stream()
                            .filter(referenceBase -> referenceBase instanceof ConcreteElement)
                            .filter(referenceBase -> !(referenceBase.getElement().getParent() instanceof XsdGroup))
                            .map(referenceBase -> (ConcreteElement) referenceBase)
                            .collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();
    }

}

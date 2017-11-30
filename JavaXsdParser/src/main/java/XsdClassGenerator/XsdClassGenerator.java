package XsdClassGenerator;

import XsdElements.*;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class XsdClassGenerator {

    private static final String JAVA_OBJECT = "java/lang/Object";
    private static final String JAVA_STRING = "Ljava/lang/String;";
    private static final String CONSTRUCTOR = "<init>";
    private static final String ATTRIBUTE_PREFIX = "attr";
    private static final String ATTRIBUTE_CLASS_SUFIX = "Attributes";

    private Map<String, List<XsdElement>> groupInterfaces = new HashMap<>();
    private Map<String, List<XsdAttribute>> baseClasses = new HashMap<>();

    public void generateClassFromElements(List<XsdElementBase> elementList){
        List<XsdElement> concreteElementList = elementList.stream()
                                                                .filter(element -> element instanceof XsdElement)
                                                                .map(element -> (XsdElement) element)
                                                                .collect(Collectors.toList());

        XsdClassGeneratorUtils.createGeneratedFilesDirectory();

        concreteElementList.forEach(this::generateClassFromElement);

        generateInterfaces();

        generateBaseClasses();
    }

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param element The element from which the class will be generated.
     */
    private void generateClassFromElement(XsdElement element) {
        String className = XsdClassGeneratorUtils.toCamelCase(element.getName());

        List<XsdElement> elementChildren = getOwnChildren(element);
        List<XsdAttribute> elementAttributes = getOwnAttributes(element);
        String superClass = getSuperClassName(element);
        String[] interfaces = getSharedInterfaces(element);

        ClassWriter classWriter = generateClass(className, superClass, interfaces);

        generateConstructor(classWriter, superClass, ACC_PUBLIC);

        generateMethods(classWriter, elementChildren);

        generateFields(classWriter, elementAttributes, className);

        classWriter.visitEnd();

        XsdClassGeneratorUtils.writeClassToFile(className, classWriter.toByteArray());
    }

    private void generateInterfaces() {
        groupInterfaces.keySet().forEach(this::generateInterface);
    }

    private void generateBaseClasses() {
        baseClasses.keySet().forEach(this::generateBaseClass);
    }

    private void generateBaseClass(String baseClassName){
        String baseClassNameCamelCase = XsdClassGeneratorUtils.toCamelCase(baseClassName);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, new String[]{}, ACC_PUBLIC + ACC_ABSTRACT);

        generateFields(interfaceWriter, baseClasses.get(baseClassName), baseClassNameCamelCase);

        interfaceWriter.visitEnd();

        XsdClassGeneratorUtils.writeClassToFile(baseClassNameCamelCase, interfaceWriter.toByteArray());
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
     * @param children The children of the element which generated the class. Their name represents a
     */
    private void generateMethods(ClassWriter classWriter, List<XsdElement> children) {
        for (XsdElement child : children) {
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
     * @param elementAttributes The name of the fields to be created. (Only String fields are being supported)
     * @param className The class name from the class where the field will be added.
     */
    private void generateFields(ClassWriter classWriter, List<XsdAttribute> elementAttributes, String className) {
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        String fullClassName = XsdClassGeneratorUtils.getFullClassTypeName(className);

        for (XsdAttribute elementAttribute : elementAttributes) {
            String name = ATTRIBUTE_PREFIX + XsdClassGeneratorUtils.toCamelCase(elementAttribute.getName()).replaceAll("\\W+", "");
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

            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "set" + camelCaseName, "(" + JAVA_STRING +")" + fullClassName, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, fullClassName, name, JAVA_STRING);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
    }

    /**
     * Generates a default constructor and another with all the parameters.
     * @param classWriter The class writer from the class where the constructors will be added.
     * @param constructorType The modifiers for the constructor
     */
    private void generateConstructor(ClassWriter classWriter, String baseClass, int constructorType) {
        MethodVisitor defaultConstructor = classWriter.visitMethod(constructorType, CONSTRUCTOR, "()V",null,null);

        defaultConstructor.visitCode();
        defaultConstructor.visitVarInsn(ALOAD, 0);
        defaultConstructor.visitMethodInsn(INVOKESPECIAL, XsdClassGeneratorUtils.getFullClassTypeName(baseClass), CONSTRUCTOR, "()V", false);
        defaultConstructor.visitInsn(RETURN);
        defaultConstructor.visitMaxs(1, 1);

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

    /**
     * This method obtains the interfaces which his class will be implementing.
     * The interfaces are represented in XsdElements as XsdGroups, and their respective.
     * methods as children of the XsdGroup.
     * @param element The element from which the interfaces will be obtained.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getSharedInterfaces(XsdElement element){
        String[] typeInterfaces = new String[0], groupInterfaces = new String[0];
        XsdElementBase typeWrapper = element.getXsdType();

        if (typeWrapper != null && typeWrapper instanceof XsdComplexType){

            typeInterfaces = getSharedInterfaces((XsdComplexType) typeWrapper);
        }

        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            groupInterfaces = getSharedInterfaces(complexType);
        }

        return ArrayUtils.addAll(typeInterfaces, groupInterfaces);
    }

    /**
     * Obtains all the xsdGroups in the given element which will be used to create interfaces.
     * @param complexType The complexType of the element which will be implementing the interfaces.
     * @return The names of all the interfaces that will be implementing.
     */
    private String[] getSharedInterfaces(XsdComplexType complexType) {
        XsdElementBase complexChildElement = complexType.getXsdChildElement();

        Map<String, List<XsdElement>> groupElements = new HashMap<>();

        if (complexChildElement instanceof XsdGroup){
            groupElements.put(((XsdGroup) complexChildElement).getName(), complexChildElement.getXsdElements().stream().map(element -> (XsdElement) element).collect(Collectors.toList()));
        }

        if (complexChildElement instanceof XsdMultipleElements){
            groupElements = ((XsdMultipleElements) complexChildElement).getGroupElements();
        }

        return getInterfaceNames(groupElements);
    }

    /**
     * This method will populate the groupInterfaces field with all the interface information
     * that will be obtained while creating the classes in order to create all the needed interfaces
     * afterwards.
     * @param groupElements The Map containing the information about interfaces from a given element.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getInterfaceNames(Map<String, List<XsdElement>> groupElements) {
        String[] groupNames = new String[groupElements.keySet().size()];
        groupNames = groupElements.keySet().toArray(groupNames);

        groupElements.keySet().forEach((String groupName) -> {
            if (!groupInterfaces.containsKey(groupName)){
                groupInterfaces.put(
                        XsdClassGeneratorUtils.getInterfaceName(groupName),
                        groupElements.get(groupName));
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
    private List<XsdElement> getOwnChildren(XsdElement element) {
        if (element.getXsdComplexType() != null){
            XsdElementBase elementWrapper = element.getXsdComplexType().getXsdChildElement();

            if (elementWrapper != null) {
                return elementWrapper
                        .getXsdElements()
                        .stream()
                        .filter(referenceBase -> !(referenceBase.getParent() instanceof XsdGroup))
                        .map(referenceBase -> (XsdElement) referenceBase)
                        .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }

    /**
     * Obtains this element super class name. It obtains all the super classes from the attributes
     * and uses getBaseClass to recursively iterate on its parent until it reaches a common parent
     * or creates a combination of two groups of attributes.
     * @param element The element that contains the attributes
     * @return The elements super class name.
     */
    private String getSuperClassName(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            List<XsdAttributeGroup> baseClasses = complexType.getXsdAttributes()
                                                                .stream()
                                                                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                                                                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                                                                .distinct()
                                                                .collect(Collectors.toList());

            if (baseClasses != null && !baseClasses.isEmpty()){
                return XsdClassGeneratorUtils.getFullClassTypeName(XsdClassGeneratorUtils.toCamelCase(getBaseClass(element, baseClasses)));
            }
        }

        return JAVA_OBJECT;
    }

    /**
     * Recursively iterates in parents of attributes in order to try finding a common attribute group.
     * @param element The element which contains the attributes.
     * @param baseClasses The attributeGroups contained in the element.
     * @return The elements super class name.
     */
    private String getBaseClass(XsdElement element, List<XsdAttributeGroup> baseClasses){
        List<XsdAttributeGroup> parents = new ArrayList<>();

        baseClasses.forEach(baseClass -> {
            addBaseClass(baseClass);

            XsdAttributeGroup parent = (XsdAttributeGroup) baseClass.getParent();

            if (!parents.contains(parent) && parent != null){
                parents.add(parent);
            }
        });

        if (baseClasses.size() == 1){
            return baseClasses.iterator().next().getName();
        }

        if (parents.size() == 0){
            return createSpecificElementBase(element, baseClasses);
        }

        return getBaseClass(element, parents);
    }

    /**
     * Obtains the attributes which are specific to the given element.
     * @param element The element containing the attributes.
     * @return A list of attributes that are exclusive to the element.
     */
    private List<XsdAttribute> getOwnAttributes(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            return complexType.getXsdAttributes()
                                .stream()
                                .filter(attribute -> attribute.getParent().equals(complexType))
                                .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }


    /**
     * Adds information about the base classes to the baseClasses variable.
     * @param baseClass The baseClass to add.
     */
    private void addBaseClass(XsdAttributeGroup baseClass) {
        if (!this.baseClasses.containsKey(baseClass.getName())){
            List<XsdAttribute> ownElements = baseClass.getXsdElements()
                    .stream()
                    .filter(attribute -> attribute.getParent().equals(baseClass))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            this.baseClasses.put(baseClass.getName(), ownElements);
        }
    }

    /**
     * Called when a Element has a combination of attribute groups. This method combines both
     * attribute groups in order to create a single one, which will be a specific base class for
     * this element
     * @param element The element which has multiple attribute groups
     * @param baseClasses The base classes to join.
     * @return The base class name, which the Element will be extending from.
     */
    private String createSpecificElementBase(XsdElement element, List<XsdAttributeGroup> baseClasses) {
        String baseClassName = element.getName() + ATTRIBUTE_CLASS_SUFIX;

        List<XsdAttribute> elements = new ArrayList<>();

        baseClasses.forEach(elementObj ->
                elements.addAll(elementObj.getXsdElements()
                        .stream()
                        .map(attribute -> (XsdAttribute) attribute)
                        .collect(Collectors.toList())));

        this.baseClasses.put(baseClassName, elements);

        return baseClassName;
    }

}

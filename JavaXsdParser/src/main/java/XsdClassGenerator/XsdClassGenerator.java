package XsdClassGenerator;

import XsdElements.*;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.ASMifier;

import java.util.*;
import java.util.stream.Collectors;

import static XsdClassGenerator.XsdClassGeneratorUtils.*;
import static org.objectweb.asm.Opcodes.*;

public class XsdClassGenerator {

    static final String JAVA_OBJECT = "java/lang/Object";
    static final String JAVA_OBJECT_DESC = "Ljava/lang/Object;";
    static final String JAVA_STRING_DESC = "Ljava/lang/String;";
    static final String JAVA_LIST = "java/util/List";
    static final String JAVA_LIST_DESC = "Ljava/util/List;";
    static final String CONSTRUCTOR = "<init>";
    static final String IELEMENT = "IElement";
    static final String IATTRIBUTE = "IAttribute";
    static final String ABSTRACT_ELEMENT = "AbstractElement";
    static final String TEXT_CLASS = "Text";

    static final String TEXT_TYPE = getFullClassTypeName(TEXT_CLASS);
    static final String TEXT_TYPE_DESC = getFullClassTypeNameDesc(TEXT_CLASS);
    static final String ABSTRACT_ELEMENT_TYPE = getFullClassTypeName(ABSTRACT_ELEMENT);
    static final String ABSTRACT_ELEMENT_TYPE_DESC = getFullClassTypeNameDesc(ABSTRACT_ELEMENT);
    static final String IELEMENT_TYPE = getFullClassTypeName(IELEMENT);
    static final String IELEMENT_TYPE_DESC = getFullClassTypeNameDesc(IELEMENT);
    static final String IATTRIBUTE_TYPE_DESC = getFullClassTypeNameDesc(IATTRIBUTE);

    private static final String ATTRIBUTE_PREFIX = "Attr";
    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFERENCE = "Alt";

    private Map<String, List<XsdElement>> elementGroupInterfaces = new HashMap<>();
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();
    private List<XsdAttribute> createdAttributes = new ArrayList<>();

    public void generateClassFromElements(List<XsdElementBase> elementList){
        DEBUGcallASMifier();

        List<XsdElement> concreteElementList = elementList.stream()
                                                            .filter(element -> element instanceof XsdElement)
                                                            .map(element -> (XsdElement) element)
                                                            .collect(Collectors.toList());

        createGeneratedFilesDirectory();

        createSupportingInfrastructure();

        concreteElementList.forEach(this::generateClassFromElement);

        generateInterfaces();

        generateAttributesGroupInterfaces();
    }

    private void DEBUGcallASMifier() {
        try {
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\JavaXsdParser\\target\\classes\\ASMSamples\\ElementBasedClasses\\ICommonAttributeGroup.class"});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param element The element from which the class will be generated.
     */
    private void generateClassFromElement(XsdElement element) {
        String className = toCamelCase(element.getName());

        List<XsdElement> elementChildren = getOwnChildren(element);
        List<XsdAttribute> elementAttributes = getOwnAttributes(element);
        String[] interfaces = getInterfaces(element);

        String signature = getClassSignature(interfaces, className);

        ClassWriter classWriter = generateClass(className, ABSTRACT_ELEMENT_TYPE, interfaces, signature,ACC_PUBLIC + ACC_SUPER);

        generateConstructor(classWriter, ABSTRACT_ELEMENT, ACC_PUBLIC);

        generateClassSpecificMethods(classWriter, className);

        generateMethodsForElements(classWriter, elementChildren, className);

        generateMethodsForAttributes(classWriter, elementAttributes);

        writeClassToFile(className, classWriter);
    }

    private void generateClassSpecificMethods(ClassWriter classWriter, String className) {
        String classType = getFullClassTypeName(className);
        String classTypeDesc = getFullClassTypeNameDesc(className);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, TEXT_TYPE);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, TEXT_TYPE, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + ABSTRACT_ELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, TEXT_TYPE);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, TEXT_TYPE, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + ABSTRACT_ELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + classTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "self", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

    }

    private void generateInterfaces() {
        elementGroupInterfaces.keySet().forEach(this::generateInterface);
    }

    private void generateAttributesGroupInterfaces() {
        attributeGroupInterfaces.keySet().forEach(this::generateAttributesGroupInterface);
    }

    private void generateAttributesGroupInterface(String attributeGroupName){
        AttributeHierarchyItem attributeHierarchyItem = attributeGroupInterfaces.get(attributeGroupName);

        String baseClassNameCamelCase = toCamelCase(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(attributeHierarchyItem.getParentsName(), interfaces);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE);

        for (XsdAttribute element : attributeHierarchyItem.getOwnElements()) {
            if (createdAttributes.stream().anyMatch(createdAttribute -> createdAttribute.getName().equalsIgnoreCase(element.getName()))){
                element.setName(element.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFERENCE);
            }
        }

        generateMethodsForAttributes(interfaceWriter, attributeHierarchyItem.getOwnElements());

        attributeHierarchyItem.getOwnElements().forEach(this::generateAttribute);

        createdAttributes.addAll(attributeHierarchyItem.getOwnElements());

        writeClassToFile(baseClassNameCamelCase, interfaceWriter);
    }

    private void generateAttribute(XsdAttribute attribute){
        String camelAttributeName = ATTRIBUTE_PREFIX + toCamelCase(attribute.getName()).replaceAll("\\W+", "");

        ClassWriter attributeWriter = generateClass(camelAttributeName, JAVA_OBJECT, new String[]{IATTRIBUTE }, null, ACC_PUBLIC);

        generateConstructor(attributeWriter, JAVA_OBJECT, ACC_PUBLIC);

        writeClassToFile(camelAttributeName, attributeWriter);
    }

    /**
     * Generates a interface with all its methods. It uses the information gathered about in elementGroupInterfaces
     * @param interfaceName The interface name.
     */
    private void generateInterface(String interfaceName){
        ClassWriter interfaceWriter = generateClass(interfaceName, JAVA_OBJECT, new String[]{ IELEMENT },"<T::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;" ,ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE);

        generateMethodsForElements(interfaceWriter, elementGroupInterfaces.get(interfaceName), interfaceName);

        writeClassToFile(interfaceName, interfaceWriter);
    }

    /**
     * Generates all the method from a given class.
     * @param classWriter The class where the method will be written.
     * @param children The children of the element which generated the class. Their name represents a method.
     * @param className The name of the class which contains the children elements.
     */
    private void generateMethodsForElements(ClassWriter classWriter, List<XsdElement> children, String className) {
        String classType = getFullClassTypeName(className);
        Set<String> createdChildren = new HashSet<>();

        for (XsdElement child : children) {
            String childCamelName = toCamelCase(child.getName());

            if (createdChildren.contains(childCamelName)){
                continue;
            }

            createdChildren.add(childCamelName);

            String childType = getFullClassTypeName(childCamelName);
            String childTypeDesc = getFullClassTypeNameDesc(childCamelName);

            MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + ")" + childTypeDesc, null, null);
            mVisitor.visitCode();
            mVisitor.visitTypeInsn(NEW, childType);
            mVisitor.visitInsn(DUP);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
            mVisitor.visitVarInsn(ASTORE, 2);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitVarInsn(ALOAD, 2);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + ABSTRACT_ELEMENT_TYPE_DESC + ")V", true);
            mVisitor.visitVarInsn(ALOAD, 2);
            mVisitor.visitInsn(ARETURN);
            mVisitor.visitMaxs(3, 3);
            mVisitor.visitEnd();

            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")TT;", null);
            mVisitor.visitCode();
            mVisitor.visitTypeInsn(NEW, childType);
            mVisitor.visitInsn(DUP);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitVarInsn(ALOAD, 2);
            mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", false);
            mVisitor.visitVarInsn(ASTORE, 3);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitVarInsn(ALOAD, 3);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + ABSTRACT_ELEMENT_TYPE_DESC + ")V", true);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "self", "()" + IELEMENT_TYPE_DESC, true);
            mVisitor.visitInsn(ARETURN);
            mVisitor.visitMaxs(4, 4);
            mVisitor.visitEnd();
        }
    }

    /**
     * Generates field for a given class.
     * @param classWriter The class where the fields will be added.
     * @param elementAttributes The name of the fields to be created. (Only String fields are being supported)
     */
    private void generateMethodsForAttributes(ClassWriter classWriter, List<XsdAttribute> elementAttributes) {
        MethodVisitor mVisitor;

        for (XsdAttribute elementAttribute : elementAttributes) {
            String camelCaseName = ATTRIBUTE_PREFIX + toCamelCase(elementAttribute.getName()).replaceAll("\\W+", "");
            String attributeClassTypeDesc = getFullClassTypeNameDesc(camelCaseName);

            mVisitor = classWriter.visitMethod(ACC_PUBLIC, "add" + camelCaseName, "(" + attributeClassTypeDesc + ")" + IELEMENT_TYPE_DESC, "(" + attributeClassTypeDesc + ")TT;", null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitTypeInsn(CHECKCAST, ABSTRACT_ELEMENT_TYPE);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, ABSTRACT_ELEMENT_TYPE, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", true);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
            mVisitor.visitInsn(ARETURN);
            mVisitor.visitMaxs(2, 2);
            mVisitor.visitEnd();
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
        defaultConstructor.visitMethodInsn(INVOKESPECIAL, getFullClassTypeName(baseClass), CONSTRUCTOR, "()V", false);
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
    static ClassWriter generateClass(String className, String superName, String[] interfaces, String signature, int classModifiers) {
        ClassWriter classWriter = new ClassWriter(0);

        if (interfaces != null){
            for (int i = 0; i < interfaces.length; i++) {
                interfaces[i] = getFullClassTypeName(interfaces[i]);
            }
        }

        classWriter.visit(V1_8, classModifiers, getFullClassTypeName(className), signature, superName, interfaces);

        return classWriter;
    }

    /**
     * This method obtains the interfaces which his class will be implementing.
     * The interfaces are represented in XsdElements as XsdGroups, and their respective.
     * methods as children of the XsdGroup.
     * @param element The element from which the interfaces will be obtained.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getElementGroupInterfaces(XsdElement element){
        String[] typeInterfaces = new String[0], groupInterfaces = new String[0];
        XsdElementBase typeWrapper = element.getXsdType();

        if (typeWrapper != null && typeWrapper instanceof XsdComplexType){

            typeInterfaces = getElementGroupInterfaces((XsdComplexType) typeWrapper);
        }

        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            groupInterfaces = getElementGroupInterfaces(complexType);
        }

        return ArrayUtils.addAll(typeInterfaces, groupInterfaces);
    }

    /**
     * Obtains all the xsdGroups in the given element which will be used to create interfaces.
     * @param complexType The complexType of the element which will be implementing the interfaces.
     * @return The names of all the interfaces that will be implementing.
     */
    private String[] getElementGroupInterfaces(XsdComplexType complexType) {
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
     * This method will populate the elementGroupInterfaces field with all the interface information
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
            if (!elementGroupInterfaces.containsKey(groupName)){
                elementGroupInterfaces.put(
                        getInterfaceName(groupName),
                        groupElements.get(groupName));
            }
        });

        for (int i = 0; i < groupNames.length ; i++) {
            groupNames[i] = getInterfaceName(groupNames[i]);
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
            XsdElementBase childElement = element.getXsdComplexType().getXsdChildElement();

            if (childElement != null) {
                return childElement
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
     * and uses getBaseAttributeGroupInterface to recursively iterate on its parent until it reaches a common parent
     * or creates a combination of two groups of attributes.
     * @param element The element that contains the attributes
     * @return The elements super class name.
     */
    private String[] getAttributeGroupInterfaces(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            List<XsdAttributeGroup> attributeGroups = complexType.getXsdAttributes()
                                                                .stream()
                                                                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                                                                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                                                                .distinct()
                                                                .collect(Collectors.toList());

            attributeGroups.addAll(complexType.getXsdAttributeGroup());

            attributeGroups = attributeGroups.stream().distinct().collect(Collectors.toList());

            attributeGroups.forEach(this::addAttributeGroup);

            if (!attributeGroups.isEmpty()){
                return getBaseAttributeGroupInterface(complexType.getXsdAttributeGroup());
            }
        }

        return new String[0];
    }

    /**
     * Recursively iterates in parents of attributes in order to try finding a common attribute group.
     * @param attributeGroups The attributeGroups contained in the element.
     * @return The elements super class name.
     */
    private String[] getBaseAttributeGroupInterface(List<XsdAttributeGroup> attributeGroups){
        List<XsdAttributeGroup> parents = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> {
            XsdAttributeGroup parent = (XsdAttributeGroup) attributeGroup.getParent();

            if (!parents.contains(parent) && parent != null){
                parents.add(parent);
            }
        });

        if (attributeGroups.size() == 1){
            return new String[]{ getInterfaceName(toCamelCase(attributeGroups.iterator().next().getName())) };
        }

        if (parents.size() == 0){
            return attributeGroups.stream()
                              .map(baseClass -> getInterfaceName(toCamelCase(baseClass.getName())))
                              .collect(Collectors.toList())
                              .toArray(new String[attributeGroups.size()]);
        }

        return getBaseAttributeGroupInterface(parents);
    }

    private String[] getInterfaces(XsdElement element) {
        String[] attributeGroupInterfaces =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfaces =  getElementGroupInterfaces(element);

        /*
        for (int i = 0; i < elementGroupInterfaces.length; i++) {
            if (!elementGroupInterfaces[i].startsWith("XsdClassGenerator")){
                elementGroupInterfaces[i] = getFullClassTypeName(elementGroupInterfaces[i]);
            }
        }
        */
        return ArrayUtils.addAll(attributeGroupInterfaces, elementGroupInterfaces);
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
     * Adds information about the base classes to the attributeGroupInterfaces variable.
     * @param attributeGroup The attributeGroup to add.
     */
    private void addAttributeGroup(XsdAttributeGroup attributeGroup) {
        if (!this.attributeGroupInterfaces.containsKey(attributeGroup.getName())){
            List<XsdAttribute> ownElements = attributeGroup.getXsdElements()
                    .stream()
                    .filter(attribute -> attribute.getParent().equals(attributeGroup))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            List<String> parentNames = attributeGroup.getAttributeGroups().stream().map(XsdReferenceElement::getName).collect(Collectors.toList());
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(attributeGroup.getName(), parentNames, ownElements);

            this.attributeGroupInterfaces.put(getInterfaceName(attributeGroup.getName()), attributeHierarchyItemItem);
        }
    }

    private String getClassSignature(String[] interfaces, String className) {
        StringBuilder signature = new StringBuilder("L" + ABSTRACT_ELEMENT_TYPE + "<" + getFullClassTypeNameDesc(className) + ">;");

        for (String anInterface : interfaces) {
            signature.append("L")
                     .append(getFullClassTypeName(anInterface))
                     .append("<")
                     .append(getFullClassTypeNameDesc(className))
                     .append(">;");
        }

        return signature.toString();
    }

    private StringBuilder getAttributeGroupSignature(List<String> parentsName, String[] interfaces) {
        StringBuilder signature;

        if (parentsName.isEmpty()){
            signature = new StringBuilder("<T:L" + ABSTRACT_ELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;");
        } else {
            signature = new StringBuilder("<T:L" + ABSTRACT_ELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC);

            for (int i = 0; i < parentsName.size(); i++) {
                signature.append("L").append(interfaces[i]).append("<TT;>;");
            }
        }

        return signature;
    }

    private String[] getAttributeGroupObjectInterfaces(List<String> parentsName) {
        String[] interfaces;

        if (parentsName.isEmpty()){
            interfaces = new String[]{ IELEMENT };
        } else {
            interfaces = new String[parentsName.size()];

            for (int i = 0; i < parentsName.size(); i++) {
                interfaces[i] = getInterfaceName(toCamelCase(parentsName.get(i)));
            }
        }

        return interfaces;
    }
}

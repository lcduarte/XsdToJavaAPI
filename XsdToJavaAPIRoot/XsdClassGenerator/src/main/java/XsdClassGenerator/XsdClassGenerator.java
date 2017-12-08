package XsdClassGenerator;

import XsdElements.*;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassWriter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    static final String ATTRIBUTE_PREFIX = "Attr";
    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFERENCE = "Alt";

    private Map<String, Stream<XsdElement>> elementGroupInterfaces = new HashMap<>();
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();
    private List<XsdAttribute> createdAttributes = new ArrayList<>();

    /**
     * Creates classes based on the elements received. Also creates all the supporting
     * infrastructure.
     * @param elements The elements from which the classes information will be obtained.
     */
    public void generateClassFromElements(Stream<XsdElementBase> elements){
        createGeneratedFilesDirectory();

        createSupportingInfrastructure();

        elements.filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .forEach(this::generateClassFromElement);

        generateInterfaces();
    }

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param element The element from which the class will be generated.
     */
    private void generateClassFromElement(XsdElement element) {
        String className = toCamelCase(element.getName());

        Stream<XsdElement> elementChildren = getOwnChildren(element);
        Stream<XsdAttribute> elementAttributes = getOwnAttributes(element);
        String[] interfaces = getInterfaces(element);

        String signature = getClassSignature(interfaces, className);

        ClassWriter classWriter = generateClass(className, ABSTRACT_ELEMENT_TYPE, interfaces, signature,ACC_PUBLIC + ACC_SUPER);

        generateConstructor(classWriter, ABSTRACT_ELEMENT, ACC_PUBLIC);

        generateClassSpecificMethods(classWriter, className);

        elementChildren.forEach(child -> generateMethodsForElement(classWriter, child, getFullClassTypeName(className)));

        elementAttributes.forEach(elementAttribute -> generateMethodsForAttribute(classWriter, elementAttribute));

        writeClassToFile(className, classWriter);
    }

    /**
     * Generates all the required interfaces, based on the information gathered while
     * creating the other classes. It creates both types of interfaces:
     * ElementGroupInterfaces - Interfaces that serve as a base to adding child elements to the current element;
     * AttributeGroupInterfaces - Interface that serve as a base to adding attributes to the current element;
     */
    private void generateInterfaces() {
        elementGroupInterfaces.keySet().forEach(this::generateElementGroupInterface);

        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface)));
    }

    /**
     * Generates a interface with all the required methods. It uses the information gathered about in elementGroupInterfaces.
     * @param interfaceName The interface name.
     */
    private void generateElementGroupInterface(String interfaceName){
        ClassWriter interfaceWriter = generateClass(interfaceName, JAVA_OBJECT, new String[]{ IELEMENT },"<T::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;" ,ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE);

        elementGroupInterfaces.get(interfaceName).forEach(child -> generateMethodsForElement(interfaceWriter, child, getFullClassTypeName(interfaceName)));

        writeClassToFile(interfaceName, interfaceWriter);
    }

    /**
     * Generates a interface with all the required methods. It uses the information gathered about in attributeGroupInterfaces.
     * @param attributeGroupName The interface name.
     * @param attributeHierarchyItem An object containing information about the methods of this interface and which interface, if any,
     *                               this interface extends.
     */
    private void generateAttributesGroupInterface(String attributeGroupName, AttributeHierarchyItem attributeHierarchyItem){
        String baseClassNameCamelCase = toCamelCase(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(interfaces);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE);

        if (attributeGroupName.equals("IButtonServerAttributeGroup")){
            int a  = 5;
        }

        attributeHierarchyItem.getOwnElements().forEach(elementAttribute -> {
            if (createdAttributes.stream().anyMatch(createdAttribute -> createdAttribute.getName().equalsIgnoreCase(elementAttribute.getName()))){
                elementAttribute.setName(elementAttribute.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFERENCE);
            }

            generateMethodsForAttribute(interfaceWriter, elementAttribute);

            generateAttribute(elementAttribute);

            createdAttributes.add(elementAttribute);
        });

        writeClassToFile(baseClassNameCamelCase, interfaceWriter);
    }

    /**
     * Creates a class which represents an attribute.
     * @param attribute The XsdAttribute type that contains the required information.
     */
    private void generateAttribute(XsdAttribute attribute){
        String camelAttributeName = ATTRIBUTE_PREFIX + toCamelCase(attribute.getName()).replaceAll("\\W+", "");

        ClassWriter attributeWriter = generateClass(camelAttributeName, JAVA_OBJECT, new String[]{IATTRIBUTE }, null, ACC_PUBLIC);

        generateConstructor(attributeWriter, JAVA_OBJECT, ACC_PUBLIC);

        writeClassToFile(camelAttributeName, attributeWriter);
    }

    /**
     * This method obtains the element interfaces which his class will be implementing.
     * The interfaces are represented in XsdElements as XsdGroups, and their respective
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
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getElementGroupInterfaces(XsdComplexType complexType) {
        XsdElementBase complexChildElement = complexType.getXsdChildElement();

        Map<String, Stream<XsdElement>> groupElements = new HashMap<>();

        if (complexChildElement instanceof XsdGroup){
            groupElements.put(((XsdGroup) complexChildElement).getName(), complexChildElement.getXsdElements().map(element -> (XsdElement) element));
        }

        if (complexChildElement instanceof XsdMultipleElements){
            groupElements = ((XsdMultipleElements) complexChildElement).getGroupElements();
        }

        storeInterfaceInformation(groupElements);

        return groupElements.keySet()
                            .stream()
                            .map(groupElement -> getInterfaceName(groupElement))
                            .toArray(String[]::new);
    }

    /**
     * This method will populate the elementGroupInterfaces field with all the interface information
     * that will be obtained while creating the classes in order to create all the required interfaces
     * afterwards.
     * @param groupElements The Map containing the information about interfaces from a given element.
     */
    private void storeInterfaceInformation(Map<String, Stream<XsdElement>> groupElements) {
        groupElements.keySet().forEach((String groupName) -> {
            if (!elementGroupInterfaces.containsKey(groupName)){
                Map<String, XsdElement> mappedElements = new HashMap<>();

                groupElements.get(groupName)
                             .forEach(elementObj -> mappedElements.put(elementObj.getName(), elementObj));

                elementGroupInterfaces.put(
                        getInterfaceName(groupName),
                        mappedElements.values().stream());
            }
        });
    }

    /**
     * Returns all the concrete children of a given element. With the separation made between
     * XsdGroups children and the remaining children this method is able to return only the
     * children that are not shared in any interface.
     * @param element The element from which the children will be obtained.
     * @return The children that are exclusive to the current element.
     */
    private Stream<XsdElement> getOwnChildren(XsdElement element) {
        if (element.getXsdComplexType() != null){
            XsdElementBase childElement = element.getXsdComplexType().getXsdChildElement();

            if (childElement != null) {
                Map<String, XsdElement> mappedElements = new HashMap<>();

                childElement
                        .getXsdElements()
                        .filter(referenceBase -> !(referenceBase.getParent() instanceof XsdGroup))
                        .map(referenceBase -> (XsdElement) referenceBase)
                        .forEach(elementObj -> mappedElements.put(elementObj.getName(), elementObj));

                return mappedElements.values().stream();
            }
        }

        return Stream.empty();
    }

    /**
     * Obtains the names of the attribute interfaces that the given element will implement.
     * @param element The element that contains the attributes.
     * @return The elements interfaces names.
     */
    private String[] getAttributeGroupInterfaces(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            List<XsdAttributeGroup> attributeGroups = complexType.getXsdAttributes()
                                                                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                                                                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                                                                .distinct()
                                                                .collect(Collectors.toList());

            attributeGroups.addAll(complexType.getXsdAttributeGroup());

            attributeGroups.stream().distinct().forEach(this::addAttributeGroup);

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
            return new String[]{ getInterfaceName(toCamelCase(attributeGroups.get(0).getName())) };
        }

        if (parents.size() == 0){
            return attributeGroups.stream()
                              .map(baseClass -> getInterfaceName(toCamelCase(baseClass.getName())))
                              .toArray(String[]::new);
        }

        return getBaseAttributeGroupInterface(parents);
    }

    /**
     * Obtains all the interfaces that a given element will implement.
     * @param element The element in which the class will be based.
     * @return A string array with all the interface names.
     */
    private String[] getInterfaces(XsdElement element) {
        String[] attributeGroupInterfaces =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfaces =  getElementGroupInterfaces(element);

        return ArrayUtils.addAll(attributeGroupInterfaces, elementGroupInterfaces);
    }

    /**
     * Obtains the attributes which are specific to the given element.
     * @param element The element containing the attributes.
     * @return A list of attributes that are exclusive to the element.
     */
    private Stream<XsdAttribute> getOwnAttributes(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            return complexType.getXsdAttributes()
                                .filter(attribute -> attribute.getParent().equals(complexType));
        }

        return Stream.empty();
    }

    /**
     * Adds information about the attribute group interface to the attributeGroupInterfaces variable.
     * @param attributeGroup The attributeGroup to add.
     */
    private void addAttributeGroup(XsdAttributeGroup attributeGroup) {
        String interfaceName = getInterfaceName(attributeGroup.getName());

        if (!attributeGroupInterfaces.containsKey(interfaceName)){
            List<XsdAttribute> ownElements = attributeGroup.getXsdElements()
                    .filter(attribute -> attribute.getParent().equals(attributeGroup))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            List<String> parentNames = attributeGroup.getAttributeGroups().stream().map(XsdReferenceElement::getName).collect(Collectors.toList());
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(attributeGroup.getName(), parentNames, ownElements);

            attributeGroupInterfaces.put(interfaceName, attributeHierarchyItemItem);
        }
    }

    /**
     * Obtains the signature for a class given the interface names.
     * @param interfaces The implemented interfaces.
     * @param className The class name.
     * @return The signature of the class.
     */
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

    /** Obtains the signature for the attribute group interfaces based on the implemented interfaces.
     * @param interfaces The implemented interfaces.
     * @return The signature of this interface.
     */
    private StringBuilder getAttributeGroupSignature(String[] interfaces) {
        StringBuilder signature;

        if (interfaces.length == 0){
            signature = new StringBuilder("<T:L" + ABSTRACT_ELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;");
        } else {
            signature = new StringBuilder("<T:L" + ABSTRACT_ELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC);

            for (String anInterface : interfaces) {
                signature.append("L").append(anInterface).append("<TT;>;");
            }
        }

        return signature;
    }

    /**
     * Obtains an array with the names of the interfaces implemented by a attribute group interface
     * with the given parents, as in interfaces that will be extended.
     * @param parentsName The list of interfaces that this interface will extend
     * @return A string array containing the names of the interfaces that this interface will extend.
     */
    private String[] getAttributeGroupObjectInterfaces(List<String> parentsName) {
        String[] interfaces;

        if (parentsName.size() == 0){
            interfaces = new String[]{ IELEMENT };
        } else {
            interfaces = new String[parentsName.size()];

            parentsName.stream().map(parentName -> getInterfaceName(toCamelCase(parentName))).collect(Collectors.toList()).toArray(interfaces);
        }

        return interfaces;
    }
}
